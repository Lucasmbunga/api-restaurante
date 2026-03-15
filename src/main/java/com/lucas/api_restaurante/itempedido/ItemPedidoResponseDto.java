package com.lucas.api_restaurante.itempedido;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemPedidoResponseDto(@NotNull ItemPedido itemPedido, List<String> acompanhantes) {
}
