package com.lucas.api_restaurante.entrada_stock;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EntradaStockResponseDto(@NotBlank @NotNull String nomeProduto, @NotNull EntradaStock entradaStock) {
}
