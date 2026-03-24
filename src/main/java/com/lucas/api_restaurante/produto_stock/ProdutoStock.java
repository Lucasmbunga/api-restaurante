package com.lucas.api_restaurante.produto_stock;

import com.lucas.api_restaurante.produto.Produto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produto_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_produto",nullable = false,unique = true)
    private Produto produto;

    private int quantidadeDisponivel=0;
}
