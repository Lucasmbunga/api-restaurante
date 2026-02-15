package com.lucas.api_restaurante.config;

import jakarta.validation.constraints.NotBlank;

public record TokenResponseDto(@NotBlank String email,@NotBlank String token) {
}
