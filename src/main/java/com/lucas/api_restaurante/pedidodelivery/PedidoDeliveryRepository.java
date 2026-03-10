package com.lucas.api_restaurante.pedidodelivery;

import com.lucas.api_restaurante.pedido.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PedidoDeliveryRepository extends JpaRepository<PedidoDelivery, Long>, JpaSpecificationExecutor<PedidoDelivery> {

}
