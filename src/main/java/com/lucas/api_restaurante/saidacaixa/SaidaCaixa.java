package com.lucas.api_restaurante.saidacaixa;

import com.lucas.api_restaurante.caixa.Caixa;
import com.lucas.api_restaurante.contasapagar.ContasAPagar;
import com.lucas.api_restaurante.turno.Turno;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "saida_caixa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaidaCaixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDateTime data;
    @NotNull
    private String descricao;
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "id_turno")
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_a_pagar")
    private ContasAPagar contasAPagar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caixa")
    private Caixa caixa;

}
