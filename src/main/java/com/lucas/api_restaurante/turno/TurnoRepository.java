package com.lucas.api_restaurante.turno;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findByStatus(StatusTurno status);
}
