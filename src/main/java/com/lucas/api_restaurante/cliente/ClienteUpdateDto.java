package com.lucas.api_restaurante.cliente;

import com.lucas.api_restaurante.endereco.Endereco;
import com.lucas.api_restaurante.telefone.Telefone;

import java.util.List;

public record ClienteUpdateDto(String nome, String email, List<String> telefones,String nif,String cidade,String bairro,String zona,String rua) {
}
