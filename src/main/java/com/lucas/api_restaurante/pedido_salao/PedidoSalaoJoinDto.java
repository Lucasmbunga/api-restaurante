package com.lucas.api_restaurante.pedido_salao;

import jakarta.validation.constraints.NotNull;

public record PedidoSalaoJoinDto(@NotNull Long idMesaOrigem,@NotNull Long idMesaDestino) {
}
