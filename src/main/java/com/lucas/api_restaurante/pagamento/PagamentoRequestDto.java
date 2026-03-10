package com.lucas.api_restaurante.pagamento;

import com.lucas.api_restaurante.pedido.Pedido;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequestDto(@NotNull Long idPedido,MetodoPagamento metodoPagamento) {
}
