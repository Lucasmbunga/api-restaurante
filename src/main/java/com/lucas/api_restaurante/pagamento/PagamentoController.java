package com.lucas.api_restaurante.pagamento;

import com.lucas.api_restaurante.exceptions.RecursoNaoEncontradoException;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagamentoResponseDto>> fazerPagamento(@RequestBody PagamentoRequestDto pagamentoRequest)throws RecursoNaoEncontradoException{
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.registrarPagamento(pagamentoRequest,"/pagamentos/"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagamentoResponseDto>>> obterTodosPagamentos(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "DataPagamento") Pageable pageable){
        return ResponseEntity.ok(pagamentoService.listarPagamentos(pageable,"/pagamentos"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagamentoResponseDto>> obterPagamentoPorId(@PathVariable Long id)throws RecursoNaoEncontradoException{
        return ResponseEntity.ok(pagamentoService.obterPagamentoPorId(id,""));
    }

    @GetMapping("/{idPedido}/pagamento")
    public ResponseEntity<ApiResponse<PagamentoResponseDto>> obterPagamentoPorPedido(@PathVariable Long idPedido)throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(pagamentoService.obterPagamentoPorPedido(idPedido,""));
    }

    @GetMapping("{idCliente}/pagamentos")
    public ResponseEntity<ApiResponse<List<PagamentoResponseDto>>> obterPagamentoPorCliente(@PathVariable Long idCliente) throws RecursoNaoEncontradoException {
        return ResponseEntity.ok(pagamentoService.obterPagamentoPorCliente(idCliente,""));
    }

}
