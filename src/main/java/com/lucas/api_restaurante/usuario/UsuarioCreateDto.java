package com.lucas.api_restaurante.usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UsuarioCreateDto(@NotBlank String nome,
                               @NotBlank String email,
                               @NotBlank String senha,
                               @NotNull TipoUsuario tipoUsuario,
                               @NotNull List<String> telefones) {
}


