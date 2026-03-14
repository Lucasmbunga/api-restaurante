package com.lucas.api_restaurante.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequestDto(
        @NotBlank String nome,
        @NotNull BigDecimal precoCompra,
        BigDecimal precoVenda,
        @NotNull TipoProduto tipoProduto,
        @NotNull Long  idCategoria) {

}
