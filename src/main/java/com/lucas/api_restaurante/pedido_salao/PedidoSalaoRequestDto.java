package com.lucas.api_restaurante.pedido_salao;

import com.lucas.api_restaurante.item_pedido.ItemPedidoRequestDto;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoSalaoRequestDto(
        @NotEmpty List<ItemPedidoRequestDto> itensDoPedido,
        Long idCliente,
        Long idMesa,
        String descricao
) {
}
