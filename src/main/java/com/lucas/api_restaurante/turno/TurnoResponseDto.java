package com.lucas.api_restaurante.turno;

import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.caixa.CaixaAberturaResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoResponseDto(
        @NotNull Long id,
        @NotNull LocalDate data,
        @NotNull LocalTime horaAbertura,
        @NotNull StatusTurno status,
        @NotBlank String responsavel,
        @NotNull CaixaAberturaResponseDto caixa
        ) {
}
