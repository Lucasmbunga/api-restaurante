package com.lucas.api_restaurante.pedido_salao;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.item_pedido.ItemPedido;
import com.lucas.api_restaurante.item_pedido.ItemPedidoDeleteRequestDto;
import com.lucas.api_restaurante.item_pedido.ItemPedidoRequestDto;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos-salao")
public class PedidoSalaoController {
    private final PedidoSalaoService pedidoSalaoService;

    public PedidoSalaoController(PedidoSalaoService pedidoSalaoService) {
        this.pedidoSalaoService = pedidoSalaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PedidoSalaoResponseDto>>> listarPedidos(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC, sort = "data") Pageable pageable) throws NotFoundException {
        return ResponseEntity.ok(pedidoSalaoService.listarPedidos(pageable, "/pedidos-delivery"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoSalaoResponseDto>> buscarPedidoPorId(@PathVariable Long id)throws NotFoundException {
        return ResponseEntity.ok(pedidoSalaoService.buscarPedidoPorId(id,"/pedidos-delivery/"+id));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<ApiResponse<List<PedidoSalaoResponseDto>>> filtrarPedidos(@RequestParam("idMesa") Long idMesa)throws NotFoundException {
        return ResponseEntity.ok(pedidoSalaoService.buscarPedidosPorMesa(idMesa,"/mesas/filtrar"));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<PedidoSalaoResponseDto>> criarPedido(@RequestBody PedidoSalaoRequestDto pedidoRequest) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalaoService.criarPedido(pedidoRequest, "/delivery_pedidos/"));
    }

    @PutMapping("/{id}/itens")
    public ResponseEntity<ApiResponse<ItemPedido>> adicionarItem(@PathVariable Long id, @RequestBody ItemPedidoRequestDto itemPedidoRequestDto) {

        return ResponseEntity.ok().body( pedidoSalaoService.adicionarItem(id, itemPedidoRequestDto,"/delivery_pedidos/"));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<PedidoSalaoResponseDto>> finalizarPedido(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoSalaoService.finalizarPedido(id, "/pedidos-delivery/"));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<PedidoSalaoResponseDto>> cancelarPedido(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoSalaoService.cancelarPedido(id, "/pedidos-delivery/"+id));
    }

    @PutMapping("/{idPedido}/{idItem}")
    public ResponseEntity<ApiResponse<Void>> excluirItemPedido(@PathVariable Long idPedido, @PathVariable Long idItem, @RequestBody ItemPedidoDeleteRequestDto itemPedidoDeleteRequestDto) {
        return ResponseEntity.ok(pedidoSalaoService.excluirItem(idPedido,idItem,itemPedidoDeleteRequestDto));
    }

    @PutMapping("/transferir")
    public ResponseEntity<ApiResponse<List<PedidoSalaoResponseDto>>> justarMesas(@RequestBody @Valid PedidoSalaoJoinDto pedidoSalaoJoinDto)throws NotFoundException {

        return ResponseEntity.ok(pedidoSalaoService.juntarMesas(pedidoSalaoJoinDto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoSalaoResponseDto>> excluirPedido(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(pedidoSalaoService.excluirPedido(id, "/pedidos-delivery/"+id));
    }

}
