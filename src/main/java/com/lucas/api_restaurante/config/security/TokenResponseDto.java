package com.lucas.api_restaurante.config.security;

import jakarta.validation.constraints.NotBlank;

public record TokenResponseDto(@NotBlank String email,@NotBlank String token) {
}
