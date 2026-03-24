package com.lucas.api_restaurante.pedido_delivery;

import com.lucas.api_restaurante.endereco.Endereco;
import com.lucas.api_restaurante.pedido.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "pedido_delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDelivery extends Pedido {
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @Column(name = "taxa_entrega")
    private BigDecimal taxaDeEntrega;
    private LocalTime previsaoChegada;
}
