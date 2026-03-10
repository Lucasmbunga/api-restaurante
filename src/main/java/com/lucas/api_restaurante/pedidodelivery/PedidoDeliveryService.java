package com.lucas.api_restaurante.pedidodelivery;

import com.lucas.api_restaurante.cliente.ClienteRepository;
import com.lucas.api_restaurante.garcom.GarcomRepository;
import com.lucas.api_restaurante.itempedido.ItemPedido;
import com.lucas.api_restaurante.itempedido.ItemPedidoDeleteRequestDto;
import com.lucas.api_restaurante.itempedido.ItemPedidoRequestDto;
import com.lucas.api_restaurante.pedido.EstadoPedido;
import com.lucas.api_restaurante.produto.Produto;
import com.lucas.api_restaurante.produto.ProdutoRepository;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.turno.TurnoService;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoDeliveryService {
    private final PedidoDeliveryRepository pedidoRepository;
    private final TurnoService turnoService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final GarcomRepository garcomRepository;


    public ApiResponse<List<PedidoDelivery>> listarPedidos(Pageable pageable, String path) {
        List<PedidoDelivery> pedidos = pedidoRepository.findAll(pageable).getContent();

        return ResponseUtil.sucess(pedidos, "Sucesso", path);
    }

    public ApiResponse<PedidoDeliveryResponseDto> buscarPedidoPorId(Long id, String path){
        var pedido=pedidoRepository.findById(id).orElseThrow(()->new RuntimeException("Pedido não encontrado"));
        return ResponseUtil.sucess(new PedidoDeliveryResponseDto(pedido.getItens(),pedido.getCliente().getNome(),pedido.getGarcom().getNome(),pedido.getValorTotal(),pedido.getDescricao(),pedido.getEstado()),"Sucesso",path);
    }

    public ApiResponse<List<PedidoDelivery>> filtrarPedidos(Pageable pageable, EstadoPedido estado, LocalDate data, LocalTime hora, String path) {
        Specification<PedidoDelivery> pedidos = Specification.allOf(
                        DeliverySpacification.comEstadoPedido(estado))
                .and(DeliverySpacification.comData(data))
                .and(DeliverySpacification.comHora(hora));

        return ResponseUtil.sucess(pedidoRepository.findAll(pedidos, pageable).getContent(), "Sucesso", path);

    }

    @Transactional
    public ApiResponse<PedidoDeliveryResponseDto> criarPedido(PedidoDeliveryRequestDto pedido, String path) {
        var turnoAtivo = turnoService.obterTurnoAtivo().data();

        if (turnoAtivo.isEmpty()) {
            throw new RuntimeException("Não foi possível criar o pedido porque para criar um pedido deve ter um turno aberto.");
        }

        PedidoDelivery novoPedido = new PedidoDelivery();
        novoPedido.setData(LocalDate.now());
        novoPedido.setHora(LocalTime.now());
        novoPedido.setEstado(EstadoPedido.ABERTO);
        novoPedido.setTurno(turnoAtivo.get());
        novoPedido.setPrevisaoChegada(pedido.previsaoChegada());

        var cliente = clienteRepository.findByEmail(pedido.email()).orElseThrow(() -> new RuntimeException("Não foi possível efetuar este pedido porque o email introduzido não é válido."));
        novoPedido.setCliente(cliente);
        novoPedido.setDescricao(pedido.descricao());

        var usuario = SecurityContextHolder.getContext().getAuthentication();

        var garcom = garcomRepository.findById(((Usuario) usuario.getPrincipal()).getId()).orElseThrow(() -> new RuntimeException("Garcom não logado"));
        novoPedido.setGarcom(garcom);

        List<ItemPedido> itens = pedido.itensDoPedido().stream().map(item -> {
            var produto = produtoRepository.findById(item.idProduto()).orElseThrow(()->new RuntimeException("Produto não encontrado"));

            var precoTotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(item.quantidade()));

            return new ItemPedido(item.quantidade(), produto.getPrecoVenda(), precoTotal, item.observacao(), produto, novoPedido);

        }).collect(Collectors.toList());

        novoPedido.setItens(itens);
        var valorTotal = itens.stream().map(ItemPedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        novoPedido.setValorTotal(valorTotal);
        var pedidoCriado = pedidoRepository.save(novoPedido);
        return ResponseUtil.sucess(this.responseDto(pedidoCriado), "Pedido criado com sucesso", path + pedidoCriado.getId());
    }

    private PedidoDeliveryResponseDto responseDto(PedidoDelivery pedido) {
        return new PedidoDeliveryResponseDto(
                pedido.getItens(),
                pedido.getCliente().getNome(),
                pedido.getGarcom().getNome(),
                pedido.getValorTotal(),
                pedido.getDescricao(),
                pedido.getEstado()
        );
    }

    public ApiResponse<ItemPedido> adicionarItem(Long idPedido, ItemPedidoRequestDto itemRequestDto, String path) {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração

        var pedido = this.verificarEstadoPedido(idPedido, "Não foi possível efetuar esta operação porque este pedido já não está aberto");

        if (itemRequestDto.quantidade() < 0) {
            throw new RuntimeException("A quantidade do produto deve ser um número maior que zero");
        }

        Produto produto = produtoRepository.findById(itemRequestDto.idProduto()).orElseThrow(() -> new RuntimeException("Não foi encontrado um produto com id " + itemRequestDto.idProduto()));

        for (int i = 0; i < pedido.getItens().size(); i++) {

            var item = pedido.getItens().get(i);
            if (item.getProduto().getId().equals(produto.getId())) {
                item.setQuantidade(item.getQuantidade() + itemRequestDto.quantidade());
                var valorTotalNovoItem = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemRequestDto.quantidade()));
                item.setPrecoTotal(item.getPrecoTotal().add(valorTotalNovoItem));

                pedido.setValorTotal(pedido.getValorTotal().add(valorTotalNovoItem));
                pedidoRepository.save(pedido);

                return ResponseUtil.sucess(item, "Item adicionado com sucesso", path + pedido.getId());
            }
        }

        var item = new ItemPedido();
        item.setQuantidade(itemRequestDto.quantidade());
        item.setPrecoUnitario(produto.getPrecoVenda());
        item.setProduto(produto);
        item.setPrecoTotal(BigDecimal.valueOf(itemRequestDto.quantidade()).multiply(produto.getPrecoVenda()));
        item.setObservacao(itemRequestDto.observacao());
        item.setPedido(pedido);

        pedido.addItem(item);
        pedido.setValorTotal(pedido.getValorTotal().add(item.getPrecoTotal()));
        pedidoRepository.save(pedido);

        return ResponseUtil.sucess(item, "Item adicionado com sucesso", path + pedido.getId());

    }

    public ApiResponse<Void> excluirItem(Long idPedido, Long idItem, ItemPedidoDeleteRequestDto itemPedidoDeleteRequestDto) {
        var pedido = this.verificarEstadoPedido(idPedido, "Não foi possível efetuar esta operação porque este pedido não está aberto");

        boolean excluisaoTotal=itemPedidoDeleteRequestDto.excluisaoTotal();
        int quantidadeAExcluir=itemPedidoDeleteRequestDto.quantidade();

        if (excluisaoTotal) {

            for (int i = 0; i < pedido.getItens().size(); i++) {

                var item = pedido.getItens().get(i);
                if (item.getId().equals(idItem)) {
                    pedido.getItens().remove(item);
                    pedido.setValorTotal(pedido.getValorTotal().subtract(item.getPrecoTotal()));
                    pedidoRepository.save(pedido);
                    return ResponseUtil.sucess("Item excluído com sucesso", "");
                }
            }
        } else {
            for (int i = 0; i < pedido.getItens().size(); i++) {

                var item = pedido.getItens().get(i);
                if (item.getId().equals(idItem)) {
                    item.setQuantidade(item.getQuantidade() - quantidadeAExcluir);
                    var valorAReduzir=item.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidadeAExcluir));
                    item.setPrecoTotal(item.getPrecoTotal().subtract(valorAReduzir));

                    pedido.setValorTotal(pedido.getValorTotal().subtract(valorAReduzir));
                    pedidoRepository.save(pedido);
                    return ResponseUtil.sucess("Item excluído com sucesso", "");
                }
            }
        }

        return null;
    }

    @Transactional
    public ApiResponse<PedidoDeliveryResponseDto> finalizarPedido(Long idPedido, String path) {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração

        var pedido = this.verificarEstadoPedido(idPedido, "Este pedido já esta fechado");

        pedido.setEstado(EstadoPedido.FECHADO);
        pedidoRepository.save(pedido);

        return ResponseUtil.sucess(this.responseDto(pedido), "Pedido fechado", path + idPedido);
    }

    public ApiResponse<PedidoDeliveryResponseDto> cancelarPedido(Long idPedido, String path) {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração
        var pedido = this.verificarEstadoPedido(idPedido, "Este pedido não  está aberto");

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        return ResponseUtil.sucess(this.responseDto(pedido), "Pedido cancelado", path);
    }

    public PedidoDelivery verificarEstadoPedido(Long idPedido, String mensagem) {
        var pedido = pedidoRepository.findById(idPedido).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!pedido.getEstado().equals(EstadoPedido.ABERTO) && !pedido.getEstado().equals(EstadoPedido.EM_PREPARO)) {
            throw new RuntimeException(mensagem);

        }

        return pedido;
    }

    public ApiResponse<Void> excluirPedido(Long id, String path) {
        pedidoRepository.deleteById(id);
        return ResponseUtil.sucess("Pedido excluído com sucesso", path);
    }

}
