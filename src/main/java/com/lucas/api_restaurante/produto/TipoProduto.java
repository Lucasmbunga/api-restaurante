package com.lucas.api_restaurante.produto;

import lombok.Getter;

@Getter
public enum TipoProduto {

    PRATO("prato"),
    BEBIDA("bebida"),
    SOBREMESA("sobremesa"),
    ENTRADA("entrada"),;
    private final String valor;
    TipoProduto(String valor) {
        this.valor = valor;
    }
}
