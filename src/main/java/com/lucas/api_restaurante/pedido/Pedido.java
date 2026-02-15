package com.lucas.api_restaurante.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.cliente.Cliente;
import com.lucas.api_restaurante.garcom.Garcom;
import com.lucas.api_restaurante.itempedido.ItemPedido;
import com.lucas.api_restaurante.turno.Turno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalTime hora;
    @NotNull
    private LocalDate data;
    @NotNull
    private BigDecimal valorTotal;
    private String descricao;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_garcom")
    private Garcom garcom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno")
    private Turno turno;

    @OneToMany(mappedBy = "pedido",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ItemPedido> itens;
}
