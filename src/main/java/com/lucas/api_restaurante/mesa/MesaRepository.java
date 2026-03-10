package com.lucas.api_restaurante.mesa;

import com.lucas.api_restaurante.pedidosalao.PedidoSalao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long>, JpaSpecificationExecutor<Mesa> {
}
