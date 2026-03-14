package com.lucas.api_restaurante.pedidomesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.mesa.Mesa;
import com.lucas.api_restaurante.pedido.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido_mesa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoMesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;
}
