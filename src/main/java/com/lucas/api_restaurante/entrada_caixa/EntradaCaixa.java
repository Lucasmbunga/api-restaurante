package com.lucas.api_restaurante.entrada_caixa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.pagamento.Pagamento;
import com.lucas.api_restaurante.turno.Turno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "entrada_caixa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntradaCaixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDateTime data;
    private String descricao;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagamento",nullable = false)
    private Pagamento pagamento;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno")
    private Turno turno;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caixa")
    private Caixa caixa;
}
