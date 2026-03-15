package com.lucas.api_restaurante.categoria;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum TipoCategoria {
    PRATOS("pratos"),
    BEBIDAS("bebidas"),
    SOBREMESAS("sobremesas"),
    ENTRADAS("entradas");
    private String valor;

    TipoCategoria(String valor) {
        this.valor = valor;
    }
}
