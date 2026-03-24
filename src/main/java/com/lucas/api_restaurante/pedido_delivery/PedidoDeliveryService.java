package com.lucas.api_restaurante.pedido_delivery;

import com.lucas.api_restaurante.acompanhante.Acompanhante;
import com.lucas.api_restaurante.acompanhante.AcompanhanteRepository;
import com.lucas.api_restaurante.cliente.ClienteRepository;
import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.garcom.GarcomRepository;
import com.lucas.api_restaurante.item_acompanhante.ItemAcompanhante;
import com.lucas.api_restaurante.item_acompanhante.ItemAcompanhanteRepository;
import com.lucas.api_restaurante.item_pedido.*;
import com.lucas.api_restaurante.pedido.EstadoPedido;
import com.lucas.api_restaurante.pedido_salao.PedidoSalao;
import com.lucas.api_restaurante.produto.Produto;
import com.lucas.api_restaurante.produto.ProdutoRepository;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.saida_stock.SaidaStockRegisterDto;
import com.lucas.api_restaurante.saida_stock.SaidaStockService;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoDeliveryService {
    private final PedidoDeliveryRepository pedidoRepository;
    private final TurnoService turnoService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final GarcomRepository garcomRepository;
    private final AcompanhanteRepository acompanhanteRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemAcompanhanteRepository    itemAcompanhanteRepository;
    private final SaidaStockService saidaStockService;


    public ApiResponse<List<PedidoDelivery>> listarPedidos(Pageable pageable, String path) {
        List<PedidoDelivery> pedidos = pedidoRepository.findAll(pageable).getContent();

        return ResponseUtil.sucess(pedidos, "Sucesso", path);
    }

    public ApiResponse<PedidoDeliveryResponseDto> buscarPedidoPorId(Long id, String path){
        var pedido=pedidoRepository.findById(id).orElseThrow(()->new RuntimeException("Pedido não encontrado"));
        return ResponseUtil.sucess(this.responseDto(pedido),"Sucesso",path);
    }

    public ApiResponse<List<PedidoDelivery>> filtrarPedidos(Pageable pageable, EstadoPedido estado, LocalDate data, LocalTime hora, String path) {
        Specification<PedidoDelivery> pedidos = Specification.allOf(
                        DeliverySpacification.comEstadoPedido(estado))
                .and(DeliverySpacification.comData(data))
                .and(DeliverySpacification.comHora(hora));

        return ResponseUtil.sucess(pedidoRepository.findAll(pedidos, pageable).getContent(), "Sucesso", path);

    }

    @Transactional
    public ApiResponse<PedidoDeliveryResponseDto> criarPedido(PedidoDeliveryRequestDto pedidoRequest, String path) throws NotFoundException {
        var turnoAtivo = turnoService.obterTurnoAtivo().data();

        if (turnoAtivo.isEmpty()) {
            throw new RuntimeException("Não foi possível criar o pedido porque para criar um pedido deve ter um turno aberto.");
        }

        PedidoDelivery novoPedido = new PedidoDelivery();
        novoPedido.setData(LocalDate.now());
        novoPedido.setHora(LocalTime.now());
        novoPedido.setEstado(EstadoPedido.ABERTO);
        novoPedido.setTurno(turnoAtivo.get());
        novoPedido.setPrevisaoChegada(pedidoRequest.previsaoChegada());

        var cliente = clienteRepository.findByEmail(pedidoRequest.email()).orElseThrow(() -> new RuntimeException("Não foi possível efetuar este pedido porque o email introduzido não é válido."));
        novoPedido.setCliente(cliente);
        novoPedido.setDescricao(pedidoRequest.descricao());

        var usuario = SecurityContextHolder.getContext().getAuthentication();

        var garcom = garcomRepository.findById(((Usuario) usuario.getPrincipal()).getId()).orElseThrow(() -> new RuntimeException("Garcom não logado"));
        novoPedido.setGarcom(garcom);

        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoRequestDto itemRequest : pedidoRequest.itensDoPedido()) {

            var produto = produtoRepository.findById(itemRequest.idProduto()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (itemRequest.quantidade() <=0) {
                throw new IllegalArgumentException("A quantidade de produtos deve ser um número maior que zero");
            }

            var precoTotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemRequest.quantidade()));

            var novoItem = new ItemPedido(itemRequest.quantidade(), produto.getPrecoVenda(), precoTotal, itemRequest.observacao(), produto, novoPedido);
            itens.add(novoItem);

        }

        novoPedido.setItens(itens);
        var valorTotal = itens.stream().map(ItemPedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        novoPedido.setValorTotal(valorTotal);
        var pedidoCriado = pedidoRepository.save(novoPedido);

        this.darBaixaNoStock(pedidoCriado);

        for (ItemPedidoRequestDto itemRequest : pedidoRequest.itensDoPedido()) {
            var produto = produtoRepository.findById(itemRequest.idProduto()).orElseThrow(() -> new NotFoundException("Não foi encontrado um pedido com id" + itemRequest.idProduto()));
            var itemPedido = itemPedidoRepository.findByProdutoAndPedido(produto,pedidoCriado);

            if(produto.getCategoria().getNome().equalsIgnoreCase("Pratos a Peixe") && itemRequest.idsAcompanhantes().isEmpty()){
                throw new  IllegalArgumentException("Selecione acompanhantes para este prato.");
            }

            if (!itemRequest.idsAcompanhantes().isEmpty()) {

                for (Long idAcompanhante : itemRequest.idsAcompanhantes()) {

                    Acompanhante acompanhante = acompanhanteRepository.findById(idAcompanhante).orElseThrow(() -> new NotFoundException("Não foi encontrado um acompanhante com id " + idAcompanhante));
                    var novoItemAcompanhante = new ItemAcompanhante();
                    novoItemAcompanhante.setItemPedido(itemPedido);
                    novoItemAcompanhante.setAcompanhante(acompanhante);
                    itemAcompanhanteRepository.save(novoItemAcompanhante);

                }
            }
        }

        return ResponseUtil.sucess(this.responseDto(pedidoCriado), "Pedido criado com sucesso", path + pedidoCriado.getId());
    }

    private void darBaixaNoStock(PedidoDelivery pedidoDelivery) throws NotFoundException {
        for (ItemPedido item : pedidoDelivery.getItens()) {
            saidaStockService.darBaixaNoStock(new SaidaStockRegisterDto(item.getProduto().getId(), item.getQuantidade()));
        }
    }

    private PedidoDeliveryResponseDto responseDto(PedidoDelivery pedido) {

        var itens = pedido.getItens().stream()
                .map(item -> {

                    List<String> nomesDeAcompanhantes = new ArrayList<>();
                    if (!acompanhanteRepository.findByItemPedido(item.getId()).isEmpty()) {

                        nomesDeAcompanhantes = acompanhanteRepository.findByItemPedido(item.getId())
                                .stream().map(Acompanhante::getNome).toList();
                    }
                    return new ItemPedidoResponseDto(item, nomesDeAcompanhantes);
                }).toList();

        return new PedidoDeliveryResponseDto(
                pedido.getId(),
                itens,
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
