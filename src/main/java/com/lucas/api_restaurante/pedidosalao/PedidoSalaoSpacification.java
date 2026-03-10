package com.lucas.api_restaurante.pedidosalao;

import com.lucas.api_restaurante.pedido.EstadoPedido;
import com.lucas.api_restaurante.pedidodelivery.PedidoDelivery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;


public class PedidoSalaoSpacification {
    public static Specification<PedidoSalao> comEstadoPedido(EstadoPedido estadoPedido){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estadoPedido"), estadoPedido));
    }
    public static Specification<PedidoSalao> comData(LocalDate data){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("data"), data));
    }
    public static Specification<PedidoSalao> comHora(LocalTime hora){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hora"), hora));
    }
}
