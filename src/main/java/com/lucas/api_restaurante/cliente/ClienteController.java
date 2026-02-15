package com.lucas.api_restaurante.cliente;

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
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponseDto>>> listarClientes(@PageableDefault(page = 0,size = 10,direction = Sort.Direction.ASC,sort = "nome")Pageable pageable){
        return ResponseEntity.ok(clienteService.listarClientes(pageable,"/api/clientes"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDto>> buscarClientePorId(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.buscarClientePorId(id,"/api/clientes/"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDto>> registrarCliente(@RequestBody ClienteRequestDto clienteRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.registrarCliente(clienteRequestDto,"/api/clientes/"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDto>> editarDadosDoCliente(@RequestBody ClienteRequestDto clienteRequestDto, @PathVariable Long id){
        return ResponseEntity.ok(clienteService.editarDadosDoCliente(id,clienteRequestDto,"/api/clientes/"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirCliente(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.excluirCliente(id,"/api/clientes/"));
    }
}
