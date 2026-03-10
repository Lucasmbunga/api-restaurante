package com.lucas.api_restaurante.itempedido;

import jakarta.validation.constraints.NotNull;

public record ItemPedidoDeleteRequestDto( @NotNull Integer quantidade,@NotNull boolean excluisaoTotal) {
}
