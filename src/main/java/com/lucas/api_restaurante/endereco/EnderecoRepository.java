package com.lucas.api_restaurante.endereco;

import com.lucas.api_restaurante.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<Endereco,Long> {
}
