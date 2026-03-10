package com.lucas.api_restaurante.pedidosalao;

import com.lucas.api_restaurante.pedido.Pedido;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pedido_salao")
@Getter
@Setter
public class PedidoSalao extends Pedido {

}
