package com.lucas.api_restaurante.pedidosalao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PedidoSalaoRepository extends JpaRepository<PedidoSalao, Long> , JpaSpecificationExecutor<PedidoSalao> {

}
