package com.lucas.api_restaurante.saida_caixa;

import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.turno.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaidaCaixaRepository extends JpaRepository<SaidaCaixa, Long> {

    List<SaidaCaixa> findByCaixa(Caixa caixa);
    List<SaidaCaixa> findByTurno(Turno turno);
}
