package com.lucas.api_restaurante.item_pedido;

import jakarta.validation.constraints.NotNull;

public record ItemPedidoDeleteRequestDto( @NotNull Integer quantidade,@NotNull boolean excluisaoTotal) {
}
