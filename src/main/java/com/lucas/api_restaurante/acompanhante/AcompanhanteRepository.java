package com.lucas.api_restaurante.acompanhante;

import com.lucas.api_restaurante.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcompanhanteRepository extends JpaRepository<Acompanhante, Long> {
    @Query("FROM Acompanhante AS a JOIN ItemAcompanhante AS ia on a.id=ia.acompanhante.id JOIN ItemPedido AS ip ON ia.itemPedido.id=:idItem")
    List<Acompanhante> findByItemPedido(@Param("idItem") Long idItem);
}
