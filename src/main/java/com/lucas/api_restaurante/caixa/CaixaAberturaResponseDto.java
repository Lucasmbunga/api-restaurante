package com.lucas.api_restaurante.caixa;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CaixaAberturaResponseDto(@NotNull Long idCaixa, BigDecimal valorInicial) {
}
