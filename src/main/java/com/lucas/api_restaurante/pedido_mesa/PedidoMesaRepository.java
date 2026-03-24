package com.lucas.api_restaurante.pedido_mesa;

import com.lucas.api_restaurante.mesa.Mesa;
import com.lucas.api_restaurante.pedido_salao.PedidoSalao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PedidoMesaRepository extends JpaRepository<PedidoMesa, Long> {
    Optional<PedidoMesa> findByMesaAndPedido(Mesa mesa,PedidoSalao pedidoSalao);
    Optional<PedidoMesa> findByPedido(PedidoSalao pedido);
}
