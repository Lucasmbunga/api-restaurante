package com.lucas.api_restaurante.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.categoria.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nome;
    @NotNull
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_produto")
    private TipoProduto tipoProduto;

    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria",nullable = false)
    private Categoria categoria;

    @Override
    public String toString(){
        return "Produto{"+
                "id="+id+
                "nome:"+nome+
                " precoCompra: "+precoCompra+
                " precoVenda: "+precoVenda+"}";
    }
}
