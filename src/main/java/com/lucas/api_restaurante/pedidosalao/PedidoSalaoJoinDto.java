package com.lucas.api_restaurante.pedidosalao;

import jakarta.validation.constraints.NotNull;

public record PedidoSalaoJoinDto(@NotNull Long idMesaOrigem,@NotNull Long idMesaDestino) {
}
