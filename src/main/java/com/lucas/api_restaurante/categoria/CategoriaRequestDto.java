package com.lucas.api_restaurante.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoriaRequestDto(@NotBlank String nome, @NotNull TipoCategoria tipoCategoria) {
}
