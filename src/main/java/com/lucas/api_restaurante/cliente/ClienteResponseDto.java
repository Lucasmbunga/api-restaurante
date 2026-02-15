package com.lucas.api_restaurante.cliente;

import com.lucas.api_restaurante.endereco.Endereco;
import com.lucas.api_restaurante.telefone.Telefone;
import com.lucas.api_restaurante.usuario.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ClienteResponseDto(@NotNull Long id, @NotBlank String nome, @NotBlank @Email String email, List<String> telefones,String nif,
                                 List<Endereco> endereco) {
}
