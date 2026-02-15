package com.lucas.api_restaurante.itempedido;

import com.lucas.api_restaurante.acompanhante.Acompanhante;
import com.lucas.api_restaurante.pedido.Pedido;
import com.lucas.api_restaurante.produto.Produto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Integer quantidade;
    @NotNull
    private BigDecimal precoUnitario;
    private BigDecimal precoTotal;
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToMany()
    @JoinTable(
            name = "item_pedido_acompanhante",
            joinColumns = @JoinColumn(name = "id_item_pedido"),
            inverseJoinColumns = @JoinColumn(name = "id_acompanhante")
    )
    private List<Acompanhante> companhantes;
}
