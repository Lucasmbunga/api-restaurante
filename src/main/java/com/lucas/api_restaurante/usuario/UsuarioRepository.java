package com.lucas.api_restaurante.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    UserDetails findByEmail(String email);
    Optional<UserDetails> findByNome(String nome);
}
