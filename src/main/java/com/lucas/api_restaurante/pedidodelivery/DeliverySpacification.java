package com.lucas.api_restaurante.pedidodelivery;

import com.lucas.api_restaurante.pedido.EstadoPedido;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;


public class DeliverySpacification {
    public static Specification<PedidoDelivery> comEstadoPedido(EstadoPedido estadoPedido){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estadoPedido"), estadoPedido));
    }
    public static Specification<PedidoDelivery> comData(LocalDate data){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("data"), data));
    }
    public static Specification<PedidoDelivery> comHora(LocalTime hora){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hora"), hora));
    }
}
