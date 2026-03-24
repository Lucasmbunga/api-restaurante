package com.lucas.api_restaurante.mesa;

import com.lucas.api_restaurante.pedido_salao.PedidoSalao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long>, JpaSpecificationExecutor<Mesa> {
    @Query("from Mesa as m  JOIN PedidoMesa as pm on m.id=pm.mesa.id  where pm.pedido.id=:idPedido")
    Optional<Mesa> findByPedido(@Param("idPedido") Long idPedido);

    @Query("FROM PedidoSalao AS ps JOIN PedidoMesa AS pm on ps.id=pm.pedido.id WHERE pm.mesa.id=:idMesa")
    Optional<List<PedidoSalao>> findPedido(@Param("idMesa") Long idMesa);
}
