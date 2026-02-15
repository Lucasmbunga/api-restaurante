package com.lucas.api_restaurante.usuario;

public enum TipoUsuario {

    GESTOR("gestor"),GARCOM("garçom"),CLIENTE("cliente");

    private final String valor;
     TipoUsuario(String valor){
         this.valor=valor;
     }
    public String getValor() {
        return valor;
    }
}
