package com.lucas.api_restaurante.cardapio;

import com.lucas.api_restaurante.produto.Produto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cardapio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome_versao;
    private Boolean ativo;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "item_cardapio",
joinColumns = @JoinColumn(name = "id_cardapio"),
        inverseJoinColumns = @JoinColumn(name = "id_produto")
)
    private List<Produto> produtos;
}
