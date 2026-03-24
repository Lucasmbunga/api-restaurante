package com.lucas.api_restaurante.saida_stock;

import jakarta.validation.constraints.NotNull;

public record SaidaStockResponseDto(@NotNull String nomeProduto,@NotNull SaidaStock saidaStock) {
}
