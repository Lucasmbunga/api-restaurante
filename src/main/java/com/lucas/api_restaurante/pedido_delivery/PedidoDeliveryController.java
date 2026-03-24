package com.lucas.api_restaurante.pedido_delivery;

import com.lucas.api_restaurante.exceptions.NotFoundException;
import com.lucas.api_restaurante.item_pedido.ItemPedido;
import com.lucas.api_restaurante.item_pedido.ItemPedidoDeleteRequestDto;
import com.lucas.api_restaurante.item_pedido.ItemPedidoRequestDto;
import com.lucas.api_restaurante.responseutils.ApiResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos-delivery")
public class PedidoDeliveryController {
    private final PedidoDeliveryService pedidoDeliveryService;

    public PedidoDeliveryController(PedidoDeliveryService pedidoDeliveryService) {
        this.pedidoDeliveryService = pedidoDeliveryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PedidoDelivery>>> listarPedidos(@PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC, sort = "data") Pageable pageable) {
        return ResponseEntity.ok(pedidoDeliveryService.listarPedidos(pageable, "/pedidos-delivery"));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoDeliveryResponseDto>> buscarPedidoPorId(@PathVariable Long id){
        return ResponseEntity.ok(pedidoDeliveryService.buscarPedidoPorId(id,"/pedidos-delivery/"+id));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<PedidoDeliveryResponseDto>> criarPedido(@RequestBody PedidoDeliveryRequestDto pedidoRequest) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDeliveryService.criarPedido(pedidoRequest, "/delivery_pedidos/"));
    }

    @PutMapping("/{id}/itens")
    public ResponseEntity<ApiResponse<ItemPedido>> adicionarItem(@PathVariable Long id, @RequestBody ItemPedidoRequestDto itemPedidoRequestDto) {

        return ResponseEntity.ok().body( pedidoDeliveryService.adicionarItem(id, itemPedidoRequestDto,"/delivery_pedidos/"));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<PedidoDeliveryResponseDto>> finalizarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoDeliveryService.finalizarPedido(id, "/pedidos-delivery/"));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<PedidoDeliveryResponseDto>> cancelarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoDeliveryService.cancelarPedido(id, "/pedidos-delivery/"+id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluirPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoDeliveryService.excluirPedido(id, "/pedidos-delivery/"+id));
    }

    @DeleteMapping("/{idPedido}/{idItem}")
    public ResponseEntity<ApiResponse<Void>> excluirItemPedido(@PathVariable Long idPedido, @PathVariable Long idItem, @RequestBody ItemPedidoDeleteRequestDto itemPedidoDeleteRequestDto) {
        return ResponseEntity.ok(pedidoDeliveryService.excluirItem(idPedido,idItem,itemPedidoDeleteRequestDto));
    }
}
