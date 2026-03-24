package com.lucas.api_restaurante.pedido_salao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoSalaoRepository extends JpaRepository<PedidoSalao, Long> , JpaSpecificationExecutor<PedidoSalao> {
    @Query("FROM PedidoSalao AS ps JOIN PedidoMesa AS pm ON ps.id=pm.pedido.id JOIN Mesa AS m ON pm.mesa.id=:idMesa WHERE ps.estado='ABERTO' OR ps.estado='EM_PREPARO'")
    List<PedidoSalao> findByIdMesaAndEstado(@Param("idMesa") Long idMesa);

}
