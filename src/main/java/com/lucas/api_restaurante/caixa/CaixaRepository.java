package com.lucas.api_restaurante.caixa;

import com.lucas.api_restaurante.turno.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Optional<Caixa> findByTurno(Turno turno);
}
