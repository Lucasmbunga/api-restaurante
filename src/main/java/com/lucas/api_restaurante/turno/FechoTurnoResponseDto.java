package com.lucas.api_restaurante.turno;

import com.lucas.api_restaurante.caixa.Caixa;

public record FechoTurnoResponseDto(
        Turno turnoResponse,
        Caixa caixa
) {
}
