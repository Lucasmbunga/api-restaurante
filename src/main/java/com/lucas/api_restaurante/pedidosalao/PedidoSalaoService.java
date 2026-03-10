package com.lucas.api_restaurante.pedidosalao;

import com.lucas.api_restaurante.cliente.ClienteRepository;
import com.lucas.api_restaurante.garcom.GarcomRepository;
import com.lucas.api_restaurante.itempedido.ItemPedido;
import com.lucas.api_restaurante.itempedido.ItemPedidoDeleteRequestDto;
import com.lucas.api_restaurante.itempedido.ItemPedidoRequestDto;
import com.lucas.api_restaurante.mesa.MesaRepository;
import com.lucas.api_restaurante.pedido.EstadoPedido;
import com.lucas.api_restaurante.pedidodelivery.*;
import com.lucas.api_restaurante.produto.Produto;
import com.lucas.api_restaurante.produto.ProdutoRepository;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import com.lucas.api_restaurante.turno.TurnoService;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.transaction.Transactional;
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
public class PedidoSalaoService {
    private final PedidoSalaoRepository pedidoRepository;
    private final TurnoService turnoService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final GarcomRepository garcomRepository;
    private final MesaRepository mesaRepository;

    public PedidoSalaoService(PedidoSalaoRepository pedidoRepository,
                              TurnoService turnoService, ClienteRepository clienteRepository,
                              ProdutoRepository produtoRepository, GarcomRepository garcomRepository, MesaRepository mesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.turnoService = turnoService;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.garcomRepository = garcomRepository;
        this.mesaRepository = mesaRepository;
    }

    public ApiResponse<List<PedidoSalao>> listarPedidos(Pageable pageable, String path) {
        List<PedidoSalao> pedidos = pedidoRepository.findAll(pageable).getContent();

        return ResponseUtil.sucess(pedidos, "Sucesso", path);
    }

    public ApiResponse<PedidoSalaoResponseDto> buscarPedidoPorId(Long id, String path) {
        var pedido = pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return ResponseUtil.sucess(this.responseDto(pedido), "Sucesso", path);
    }

    public ApiResponse<List<PedidoSalao>> filtrarPedidos(Pageable pageable, EstadoPedido estado, LocalDate data, LocalTime hora, String path) {
        Specification<PedidoSalao> pedidos = Specification.allOf(
                        PedidoSalaoSpacification.comEstadoPedido(estado))
                .and(PedidoSalaoSpacification.comData(data))
                .and(PedidoSalaoSpacification.comHora(hora));

        return ResponseUtil.sucess(pedidoRepository.findAll(pedidos, pageable).getContent(), "Sucesso", path);

    }

    @Transactional
    public ApiResponse<PedidoSalaoResponseDto> criarPedido(PedidoSalaoRequestDto pedido, String path) {
        var turnoAtivo = turnoService.obterTurnoAtivo().data();

        if (turnoAtivo.isEmpty()) {
            throw new RuntimeException("Não foi possível criar o pedido porque para criar um pedido deve ter um turno aberto.");
        }

        PedidoSalao novoPedido = new PedidoSalao();
        novoPedido.setData(LocalDate.now());
        novoPedido.setHora(LocalTime.now());
        novoPedido.setEstado(EstadoPedido.ABERTO);
        novoPedido.setTurno(turnoAtivo.get());
        novoPedido.setDescricao(pedido.descricao());

        if (pedido.idCliente() != null) {

            var cliente = clienteRepository.findById(pedido.idCliente()).orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
            novoPedido.setCliente(cliente);
        }


        var usuario = SecurityContextHolder.getContext().getAuthentication();
        if (usuario == null) {
            throw new RuntimeException("Usuário não logado");
        }

        var garcom = garcomRepository.findById(((Usuario) usuario.getPrincipal()).getId()).orElseThrow(() -> new RuntimeException("Garcom não encontrado"));
        novoPedido.setGarcom(garcom);

        List<ItemPedido> itens = pedido.itensDoPedido().stream().map(item -> {
            var produto = produtoRepository.findById(item.idProduto()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (item.quantidade() < 0) {
                throw new RuntimeException("A quantidade de produtos deve ser um número maior que zero");
            }
            var precoTotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(item.quantidade()));

            return new ItemPedido(item.quantidade(), produto.getPrecoVenda(), precoTotal, item.observacao(), produto, novoPedido);

        }).collect(Collectors.toList());

        novoPedido.setItens(itens);
        var valorTotal = itens.stream().map(ItemPedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        novoPedido.setValorTotal(valorTotal);
        var pedidoCriado = pedidoRepository.save(novoPedido);

        if (pedido.idMesa() != null) {
            var mesa = mesaRepository.findById(pedido.idMesa()).orElseThrow(() -> new RuntimeException("Mesa não encontrada"));
            mesa.adicionarPedido(pedidoCriado);
            mesaRepository.save(mesa);
        }

        return ResponseUtil.sucess(this.responseDto(pedidoCriado), "Pedido criado com sucesso", path + pedidoCriado.getId());
    }

    private PedidoSalaoResponseDto responseDto(PedidoSalao pedido) {
        //var mesa = mesaRepository.findContainingPedido(pedido);
        return new PedidoSalaoResponseDto(
                pedido.getItens(),
                pedido.getCliente().getNome(),
                pedido.getGarcom().getNome(),
                null,
                pedido.getValorTotal(),
                pedido.getDescricao(),
                pedido.getEstado()
        );
    }

    @Transactional
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

    @Transactional
    public ApiResponse<Void> excluirItem(Long idPedido, Long idItem, ItemPedidoDeleteRequestDto itemPedidoDeleteRequestDto) {
        var pedido = this.verificarEstadoPedido(idPedido, "Não foi possível efetuar esta operação porque este pedido não está aberto");

        if (itemPedidoDeleteRequestDto.quantidade() < 0) {
            throw new RuntimeException("A quantidade do produto deve ser um número maior que zero");
        }
        boolean excluisaoTotal = itemPedidoDeleteRequestDto.excluisaoTotal();
        int quantidadeAExcluir = itemPedidoDeleteRequestDto.quantidade();

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
                    var valorAReduzir = item.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidadeAExcluir));
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
    public ApiResponse<PedidoSalaoResponseDto> finalizarPedido(Long idPedido, String path) {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração

        var pedido = this.verificarEstadoPedido(idPedido, "Este pedido já esta fechado");

        pedido.setEstado(EstadoPedido.FECHADO);
        pedidoRepository.save(pedido);

        /*var mesa = mesaRepository.findContainingPedido(pedido);

        if (mesa.isPresent()) {
            mesa.get().excluirPedido(pedido);
            mesaRepository.save(mesa.get());
        }
      */
        return ResponseUtil.sucess(this.responseDto(pedido), "Pedido fechado", path + idPedido);
    }

    @Transactional
    public ApiResponse<PedidoSalaoResponseDto> cancelarPedido(Long idPedido, String path) {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração
        var pedido = this.verificarEstadoPedido(idPedido, "Este pedido não  está aberto");

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        /* var mesa = mesaRepository.findContainingPedido(pedido);

        if (mesa.isPresent()) {
            mesa.get().excluirPedido(pedido);
            mesaRepository.save(mesa.get());
        }
        */
        return ResponseUtil.sucess(this.responseDto(pedido), "Pedido cancelado", path);
    }

    public PedidoSalao verificarEstadoPedido(Long idPedido, String mensagem) {
        var pedido = pedidoRepository.findById(idPedido).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!pedido.getEstado().equals(EstadoPedido.ABERTO) && !pedido.getEstado().equals(EstadoPedido.EM_PREPARO)) {
            throw new RuntimeException(mensagem);

        }

        return pedido;
    }

    @Transactional
    public ApiResponse<Void> excluirPedido(Long id, String path) {
        var pedido = pedidoRepository.findById(id);

      /*  var mesa = mesaRepository.findContainingPedido(pedido.get());

        if (mesa.isPresent()) {
            mesa.get().excluirPedido(pedido.get());
            mesaRepository.save(mesa.get());
        }
       */

        pedidoRepository.deleteById(id);
        return ResponseUtil.sucess("Pedido excluído com sucesso", path);
    }
}
