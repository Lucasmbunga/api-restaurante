package com.lucas.api_restaurante.entrada_stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.produto.Produto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "entrada_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;
    private LocalTime hora;
    private int quantidade;
    private LocalDate dataValidade;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @PrePersist
    public void prePersist() {
        this.hora=LocalTime.now();
    }
}
