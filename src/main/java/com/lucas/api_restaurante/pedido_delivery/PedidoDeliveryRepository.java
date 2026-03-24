package com.lucas.api_restaurante.pedido_delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PedidoDeliveryRepository extends JpaRepository<PedidoDelivery, Long>, JpaSpecificationExecutor<PedidoDelivery> {

}
