package com.lucas.api_restaurante.saida_caixa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SaidaCaixaRequestDto(@NotNull @Positive BigDecimal valor, @NotBlank String descricao, Long idContaAPagar) {

}
