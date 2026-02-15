package com.lucas.api_restaurante.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ClienteRequestDto(@NotBlank String nome, @NotBlank @Email String email, @NotBlank String senha,
                                List<String> telefones,String nif,String cidade,String bairro,String rua,Integer zona) {
}
