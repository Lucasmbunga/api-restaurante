package com.lucas.api_restaurante.pagamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagamentoResponseDto(@NotNull Pagamento pagamento, @NotBlank String nomeCliente) {
}
