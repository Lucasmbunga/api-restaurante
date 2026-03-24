package com.lucas.api_restaurante.item_acompanhante;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.acompanhante.Acompanhante;
import com.lucas.api_restaurante.item_pedido.ItemPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_acompanhante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemAcompanhante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item_pedido")
    private ItemPedido itemPedido;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acompanhante")
    private Acompanhante acompanhante;
}
