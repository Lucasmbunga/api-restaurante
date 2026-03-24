package com.lucas.api_restaurante.item_pedido;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemPedidoResponseDto(@NotNull ItemPedido itemPedido, List<String> acompanhantes) {
}
