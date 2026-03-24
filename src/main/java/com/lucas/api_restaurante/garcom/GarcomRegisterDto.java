package com.lucas.api_restaurante.garcom;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GarcomRegisterDto(@NotBlank String nome, @NotBlank @Email String email, String senha, List<String> telefones, @NotNull CargoGarcom cargo) {
}
