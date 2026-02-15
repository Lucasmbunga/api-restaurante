package com.lucas.api_restaurante.garcom;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GarcomResponseDto(@NotNull Long id, @NotBlank String nome, @NotBlank @Email String email, @NotNull
                                CargoGarcom  cargo,List<String> telefones) {
}
