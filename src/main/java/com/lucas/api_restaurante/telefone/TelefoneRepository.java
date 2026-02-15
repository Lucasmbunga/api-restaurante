package com.lucas.api_restaurante.telefone;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelefoneRepository extends JpaRepository<Telefone,Long> {
    Optional<Telefone> findByNumeroTelefone(String numeroTelefone);
}
