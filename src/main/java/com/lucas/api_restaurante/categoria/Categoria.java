package com.lucas.api_restaurante.categoria;

import com.lucas.api_restaurante.produto.Produto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoCategoria tipoCategoria;

    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos;
}
