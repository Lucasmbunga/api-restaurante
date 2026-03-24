package com.lucas.api_restaurante.item_pedido;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemPedidoRequestDto(@NotNull Long idProduto, @NotNull int quantidade, String observacao, List<Long> idsAcompanhantes) {
}
