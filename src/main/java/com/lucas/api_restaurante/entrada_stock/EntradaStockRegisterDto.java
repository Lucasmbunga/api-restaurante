package com.lucas.api_restaurante.entrada_stock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record EntradaStockRegisterDto(@NotNull Long idProduto, @NotNull @Positive int quantidade, LocalDate dataValidade) {
}
