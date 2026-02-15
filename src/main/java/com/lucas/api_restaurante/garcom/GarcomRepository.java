package com.lucas.api_restaurante.garcom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface GarcomRepository extends JpaRepository<Garcom,Long> {
    UserDetails findByEmail(String email);

    UserDetails findByNome(String nome);
}
