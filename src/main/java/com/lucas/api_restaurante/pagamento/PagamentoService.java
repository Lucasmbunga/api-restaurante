package com.lucas.api_restaurante.pagamento;

import com.lucas.api_restaurante.caixa.CaixaRepository;
import com.lucas.api_restaurante.cliente.ClienteRepository;
import com.lucas.api_restaurante.entradacaixa.EntradaCaixaService;
import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.pedido.EstadoPedido;
import com.lucas.api_restaurante.pedido.PedidoRepository;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import com.lucas.api_restaurante.responseutils.ResponseUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final EntradaCaixaService entradaCaixaService;
    private final CaixaRepository caixaRepository;


    public PagamentoService(PagamentoRepository pagamentoRepository,
                            ClienteRepository clienteRepository,
                            PedidoRepository pedidoRepository,EntradaCaixaService entradaCaixaService,
                            CaixaRepository caixaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.entradaCaixaService = entradaCaixaService;
        this.caixaRepository = caixaRepository;
    }

    public ApiResponse<List<PagamentoResponseDto>> listarPagamentos(Pageable pageable, String path) {
        List<PagamentoResponseDto> pagamentos = pagamentoRepository.findAll(pageable).getContent().stream().map(pagamento -> {

            if(pagamento!=null){

            String nomeCliente=this.obterClienteQuePagou(pagamento);
            return new PagamentoResponseDto(pagamento,nomeCliente);
            }
            return null;
        }).toList();

        return ResponseUtil.sucess(pagamentos, "Sucesso", path);
    }

    @Transactional
    public ApiResponse<PagamentoResponseDto> registrarPagamento(PagamentoRequestDto pagamentoRequest, String path) throws RecursoNaoEncontradoException {

        var pedido = pedidoRepository.findById(pagamentoRequest.idPedido()).orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento não encontrado."));
        var pagamento = pagamentoRepository.findByPedido(pedido);

        if (pagamento.isPresent()) {
            throw new IllegalArgumentException("Este pedido já está paga.");
        } else if (!pedido.getEstado().equals(EstadoPedido.FECHADO)) {
            throw new IllegalArgumentException("Este pedido ainda não está fechado");
        }
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setPedido(pedido);
        novoPagamento.setValorPago(pedido.getValorTotal());
        novoPagamento.setDataPagamento(LocalDateTime.now());
        novoPagamento.setMetodoPagamento(pagamentoRequest.metodoPagamento());
        var pagamentoRegistrado = pagamentoRepository.save(novoPagamento);

        entradaCaixaService.darEntradaCaixa(pagamentoRegistrado,pagamentoRegistrado.getPedido().getTurno());

        String nomeCliente=this.obterClienteQuePagou(pagamentoRegistrado);
        return ResponseUtil.sucess(new PagamentoResponseDto(pagamentoRegistrado,nomeCliente), "Pagamento registrado com sucesso.", path + pagamentoRegistrado.getId());
    }

    public ApiResponse<PagamentoResponseDto> obterPagamentoPorId(Long id, String path) throws RecursoNaoEncontradoException {
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);
        if (pagamento.isEmpty()) {
            throw new RecursoNaoEncontradoException("Pagamento não encontrado.");
        }
        String nomeCliente=this.obterClienteQuePagou(pagamento.get());
        return ResponseUtil.sucess(new PagamentoResponseDto(pagamento.get(),nomeCliente), "Sucesso", path);
    }

    public ApiResponse<List<PagamentoResponseDto>> obterPagamentoPorCliente(Long idCliente, String path) throws RecursoNaoEncontradoException {
        if (!clienteRepository.existsById(idCliente)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado.");
        }
        List<Pagamento> pagamentos = pagamentoRepository.findAllByClienteId(idCliente);

        if (pagamentos.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum pagamento encontrado para este cliente .");
        }
        var pagamentoResponseList=pagamentos.stream().map(pagamento -> {

            String nomeCliente=this.obterClienteQuePagou(pagamento);

            return new PagamentoResponseDto(pagamento,nomeCliente);
        }).toList();
        return ResponseUtil.sucess(pagamentoResponseList, "Sucesso", path);
    }

    public ApiResponse<PagamentoResponseDto> obterPagamentoPorPedido(Long idPedido, String path) throws RecursoNaoEncontradoException {
        var pedido = pedidoRepository.findById(idPedido);
        if (pedido.isEmpty()) {
            throw new RecursoNaoEncontradoException("Pedido não inválido.");
        }
        Optional<Pagamento> pagamento = pagamentoRepository.findByPedido(pedido.get());

        if (pagamento.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum pagamento encontrado para este pedido .");
        }
        String nomeCliente=this.obterClienteQuePagou(pagamento.get());
        return ResponseUtil.sucess(new PagamentoResponseDto(pagamento.get(),nomeCliente), "Sucesso", path);
    }

    public String obterClienteQuePagou(Pagamento pagamento){
        return pagamento.getPedido().getCliente().getNome();
    }

}
