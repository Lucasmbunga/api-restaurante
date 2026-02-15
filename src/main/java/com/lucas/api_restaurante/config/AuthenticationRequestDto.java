package com.lucas.api_restaurante.config;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDto(@NotBlank String email,@NotBlank String senha) {
}
