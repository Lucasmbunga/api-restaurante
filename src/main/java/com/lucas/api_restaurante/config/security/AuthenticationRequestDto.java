package com.lucas.api_restaurante.config.security;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDto(@NotBlank String email,@NotBlank String senha) {
}
