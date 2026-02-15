package com.lucas.api_restaurante.garcom;

import com.lucas.api_restaurante.telefone.Telefone;
import com.lucas.api_restaurante.usuario.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GarcomCreateDto(@NotBlank String nome, @NotBlank @Email String email, String senha, List<String> telefones,@NotNull CargoGarcom cargo) {
}
