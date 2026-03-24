package com.lucas.api_restaurante.saida_stock;

import com.lucas.api_restaurante.item_pedido.ItemPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaidaStockRegisterDto(@NotNull Long idProduto, @NotNull @Positive int quantidade) {
}
