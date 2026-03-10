package com.lucas.api_restaurante.saidacaixa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaidaCixaRequestDto(@NotNull @Positive BigDecimal valor, @NotBlank String descricao, Long idContaAPagar) {

}
