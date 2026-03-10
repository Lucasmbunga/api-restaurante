package com.lucas.api_restaurante.saidacaixa;

import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.turno.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaidaCaixaRepository extends JpaRepository<SaidaCaixa, Long> {

    List<SaidaCaixa> findByCaixa(Caixa caixa);
    List<SaidaCaixa> findByTurno(Turno turno);
}
