package com.lucas.api_restaurante.pedidosalao;

import com.lucas.api_restaurante.acompanhante.Acompanhante;
import com.lucas.api_restaurante.acompanhante.AcompanhanteRepository;
import com.lucas.api_restaurante.cliente.ClienteRepository;
import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.garcom.GarcomRepository;
import com.lucas.api_restaurante.itemacompanhante.ItemAcompanhante;
import com.lucas.api_restaurante.itemacompanhante.ItemAcompanhanteRepository;
import com.lucas.api_restaurante.itempedido.*;
import com.lucas.api_restaurante.mesa.Mesa;
import com.lucas.api_restaurante.mesa.MesaRepository;
import com.lucas.api_restaurante.pedido.EstadoPedido;
import com.lucas.api_restaurante.pedidodelivery.*;
import com.lucas.api_restaurante.pedidomesa.PedidoMesa;
import com.lucas.api_restaurante.pedidomesa.PedidoMesaRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoSalaoService {
    private final PedidoSalaoRepository pedidoRepository;
    private final TurnoService turnoService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final GarcomRepository garcomRepository;
    private final MesaRepository mesaRepository;
    private final PedidoMesaRepository pedidoMesaRepository;
    private final AcompanhanteRepository acompanhanteRepository;
    private final ItemAcompanhanteRepository itemAcompanhanteRepository;
    private final ItemPedidoRepository itemPedidoRepository;


    public ApiResponse<List<PedidoSalaoResponseDto>> listarPedidos(Pageable pageable, String path) throws RecursoNaoEncontradoException {
        List<PedidoSalaoResponseDto> pedidos = new ArrayList<>();
        for (PedidoSalao pedidoSalao : pedidoRepository.findAll(pageable).getContent()) {
            PedidoSalaoResponseDto pedidoSalaoResponseDto = responseDto(pedidoSalao);
            pedidos.add(pedidoSalaoResponseDto);
        }

        return ResponseUtil.sucess(pedidos, "Sucesso", path);
    }

    public ApiResponse<PedidoSalaoResponseDto> buscarPedidoPorId(Long id, String path) throws RecursoNaoEncontradoException {
        var pedido = pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return ResponseUtil.sucess(this.responseDto(pedido), "Sucesso", path);
    }

    public ApiResponse<List<PedidoSalaoResponseDto>> filtrarPedidos(Pageable pageable, EstadoPedido estado, LocalDate data, LocalTime hora, String path) throws RecursoNaoEncontradoException {
        Specification<PedidoSalao> pedidos = Specification.allOf(
                        PedidoSalaoSpacification.comEstadoPedido(estado))
                .and(PedidoSalaoSpacification.comData(data))
                .and(PedidoSalaoSpacification.comHora(hora));

        List<PedidoSalaoResponseDto> responseDtos = new ArrayList<>();

        for (PedidoSalao pedidoSalao : pedidoRepository.findAll(pageable).getContent()) {
            PedidoSalaoResponseDto responseDto = responseDto(pedidoSalao);
            responseDtos.add(responseDto);
        }

        return ResponseUtil.sucess(responseDtos, "Sucesso", path);

    }

    public ApiResponse<List<PedidoSalaoResponseDto>> buscarPedidosPorMesa(Long idMesa, String path) throws RecursoNaoEncontradoException {
        if (!mesaRepository.existsById(idMesa)) {
            throw new RecursoNaoEncontradoException("Mesa não encontrada.");
        }

        var pedidos = mesaRepository.findPedido(idMesa).orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum pedido foiencontrado nesta mesa"));
        var response = pedidos.stream().map(pedido -> {
            try {
                return responseDto(pedido);
            } catch (RecursoNaoEncontradoException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return ResponseUtil.sucess(response, "Sucesso", path);
    }

    @Transactional
    public ApiResponse<PedidoSalaoResponseDto> criarPedido(PedidoSalaoRequestDto pedidoRequest, String path) throws RecursoNaoEncontradoException {
        var turnoAtivo = turnoService.obterTurnoAtivo().data();

        if (turnoAtivo.isEmpty()) {
            throw new RuntimeException("Não foi possível criar o pedido porque para criar um pedido deve ter um turno aberto.");
        }

        PedidoSalao novoPedido = new PedidoSalao();
        novoPedido.setData(LocalDate.now());
        novoPedido.setHora(LocalTime.now());
        novoPedido.setEstado(EstadoPedido.ABERTO);
        novoPedido.setTurno(turnoAtivo.get());
        novoPedido.setDescricao(pedidoRequest.descricao());

        if (pedidoRequest.idCliente() != null) {

            var cliente = clienteRepository.findById(pedidoRequest.idCliente()).orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
            novoPedido.setCliente(cliente);
        }

        var usuario = SecurityContextHolder.getContext().getAuthentication();
        if (usuario == null) {
            throw new RuntimeException("Usuário não logado");
        }

        var garcom = garcomRepository.findById(((Usuario) usuario.getPrincipal()).getId()).orElseThrow(() -> new RuntimeException("Garcom não encontrado"));
        novoPedido.setGarcom(garcom);

        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoRequestDto itemRequest : pedidoRequest.itensDoPedido()) {

            var produto = produtoRepository.findById(itemRequest.idProduto()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (itemRequest.quantidade() < 0) {
                throw new RuntimeException("A quantidade de produtos deve ser um número maior que zero");
            }

            var precoTotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemRequest.quantidade()));

            var novoItem = new ItemPedido(itemRequest.quantidade(), produto.getPrecoVenda(), precoTotal, itemRequest.observacao(), produto, novoPedido);
            itens.add(novoItem);

        }

        novoPedido.setItens(itens);
        var valorTotal = itens.stream().map(ItemPedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        novoPedido.setValorTotal(valorTotal);
        var pedidoCriado = pedidoRepository.save(novoPedido);

        for (ItemPedidoRequestDto itemRequest : pedidoRequest.itensDoPedido()) {
            var produto = produtoRepository.findById(itemRequest.idProduto()).orElseThrow(() -> new RecursoNaoEncontradoException("Não foi encontrado um pedido com id" + itemRequest.idProduto()));
            var itemPedido = itemPedidoRepository.findByProdutoAndPedido(produto,pedidoCriado);

            if(produto.getCategoria().getNome().equalsIgnoreCase("Pratos a Peixe") && itemRequest.idsAcompanhantes().isEmpty()){
                throw new  IllegalArgumentException("Selecione acompanhantes para este prato.");
            }

            if (!itemRequest.idsAcompanhantes().isEmpty()) {

                for (Long idAcompanhante : itemRequest.idsAcompanhantes()) {

                    Acompanhante acompanhante = acompanhanteRepository.findById(idAcompanhante).orElseThrow(() -> new RecursoNaoEncontradoException("Não foi encontrado um acompanhante com id " + idAcompanhante));
                    var novoItemAcompanhante = new ItemAcompanhante();
                    novoItemAcompanhante.setItemPedido(itemPedido);
                    novoItemAcompanhante.setAcompanhante(acompanhante);
                    itemAcompanhanteRepository.save(novoItemAcompanhante);

                }
            }

        }

        if (pedidoRequest.idMesa() != null) {
            var mesa = mesaRepository.findById(pedidoRequest.idMesa()).orElseThrow(() -> new RuntimeException("Mesa não encontrada"));
            mesa.setEstaOcupada(true);
            PedidoMesa novoPedidoMesa = new PedidoMesa();
            novoPedidoMesa.setPedido(pedidoCriado);
            novoPedidoMesa.setMesa(mesa);
            pedidoMesaRepository.save(novoPedidoMesa);
            mesaRepository.save(mesa);
        }

        return ResponseUtil.sucess(this.responseDto(pedidoCriado), "Pedido criado com sucesso", path + pedidoCriado.getId());
    }

    private PedidoSalaoResponseDto responseDto(PedidoSalao pedido) throws RecursoNaoEncontradoException {
        var mesa = mesaRepository.findByPedido(pedido.getId()).orElseThrow(() -> new RecursoNaoEncontradoException("Mesa não encontrada."));

        var itens = pedido.getItens().stream()
                .map(item -> {

                    List<String> nomesDeAcompanhantes = new ArrayList<>();
                    if (!acompanhanteRepository.findByItemPedido(item.getId()).isEmpty()) {

                        nomesDeAcompanhantes = acompanhanteRepository.findByItemPedido(item.getId())
                                .stream().map(Acompanhante::getNome).toList();
                    }
                    return new ItemPedidoResponseDto(item, nomesDeAcompanhantes);
                }).toList();

        return new PedidoSalaoResponseDto(
                pedido.getId(),
                itens,
                pedido.getCliente().getNome(),
                pedido.getGarcom().getNome(),
                mesa.getNumeroMesa(),
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
    public ApiResponse<List<PedidoSalaoResponseDto>> juntarMesas(PedidoSalaoJoinDto pedidoSalaoJoinDto) throws RecursoNaoEncontradoException {
        Mesa mesaOrigem = mesaRepository.findById(pedidoSalaoJoinDto.idMesaOrigem()).orElseThrow(() -> new RecursoNaoEncontradoException("Mesa de origem não foi encontrada."));
        Mesa mesaDestino = mesaRepository.findById(pedidoSalaoJoinDto.idMesaDestino()).orElseThrow(() -> new RecursoNaoEncontradoException("Mesa de destino não foi encontrada."));

        List<PedidoSalao> pedidos = pedidoRepository.findByIdMesaAndEstado(mesaOrigem.getId());

        if (pedidos.isEmpty()) {
            throw new IllegalArgumentException("Não foi possível efetuar esta operação. Selecione uma mesa com pedidos aberto ou em preparação.");
        }

        pedidos.forEach(pedido -> {

            PedidoMesa pedidoMesa = pedidoMesaRepository.findByMesaAndPedido(mesaOrigem, pedido).get();
            pedidoMesa.setMesa(mesaDestino);
            pedidoMesaRepository.save(pedidoMesa);

            // Ao juntar duas mesas, a mesa de origem deve ser liberada.
            liberarMesa(pedido);

        });

        List<PedidoSalaoResponseDto> responseDtos = new ArrayList<>();
        for (PedidoSalao pedido : pedidos) {
            PedidoSalaoResponseDto pedidoSalaoResponseDto = responseDto(pedido);
            responseDtos.add(pedidoSalaoResponseDto);
        }

        return ResponseUtil.sucess(responseDtos, "Sucesso", "");
    }

    @Transactional
    public ApiResponse<PedidoSalaoResponseDto> finalizarPedido(Long idPedido, String path) throws RecursoNaoEncontradoException {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração

        var pedido = this.verificarEstadoPedido(idPedido, "Este pedido já esta fechado");

        pedido.setEstado(EstadoPedido.FECHADO);
        pedidoRepository.save(pedido);

        // Ao finalizar um pedido se não tiver mais pedidos abertos ou em preparação na mesa onde estava este pedido, então esta mesa deve ser liberada.
        liberarMesa(pedido);

        return ResponseUtil.sucess(this.responseDto(pedido), "Pedido fechado com sucesso!", path + idPedido);
    }

    public void liberarMesa(PedidoSalao pedidoSalao) {

        var mesa = mesaRepository.findByPedido(pedidoSalao.getId());

        if (mesa.isPresent() && pedidoRepository.findByIdMesaAndEstado(mesa.get().getId()).isEmpty()) {
            mesa.get().setEstaOcupada(false);
            mesaRepository.save(mesa.get());
        }
    }

    @Transactional
    public ApiResponse<PedidoSalaoResponseDto> cancelarPedido(Long idPedido, String path) throws RecursoNaoEncontradoException {

        //Esta função verifica se o pedido existe e se ainda está aberto ou em preparação para sofrer alguma alteração
        var pedido = this.verificarEstadoPedido(idPedido, "Este pedido não  está aberto");

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        // Ao cancelar um pedido se não tiver mais pedidos abertos ou em preparação na mesa onde estava este pedido, então esta mesa deve ser liberada.
        liberarMesa(pedido);
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
    public ApiResponse<PedidoSalaoResponseDto> excluirPedido(Long id, String path) throws RecursoNaoEncontradoException {
        var pedido = pedidoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível efetuar esta operação. Pedido    não encontrado."));

        // Ao excluir um pedido se não tiver mais pedidos abertos ou em preparação na mesa onde estava este pedido, então esta mesa deve ser liberada.
        liberarMesa(pedido);

        pedidoRepository.delete(pedido);
        return ResponseUtil.sucess(responseDto(pedido), "Pedido excluído com sucesso", path);
    }
}
