package com.lucas.api_restaurante.usuario;


import java.util.List;

public record UsuarioDto(String nome, String email, String senha, TipoUsuario tipoUsuario, List<String> telefones) {
}
