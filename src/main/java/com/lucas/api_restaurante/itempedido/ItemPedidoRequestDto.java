package com.lucas.api_restaurante.itempedido;

import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequestDto(@NotNull Long idProduto,@NotNull int quantidade,String observacao) {
}
