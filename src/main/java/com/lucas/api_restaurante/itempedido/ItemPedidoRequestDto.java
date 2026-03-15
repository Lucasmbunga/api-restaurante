package com.lucas.api_restaurante.itempedido;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemPedidoRequestDto(@NotNull Long idProduto, @NotNull int quantidade, String observacao, List<Long> idsAcompanhantes) {
}
