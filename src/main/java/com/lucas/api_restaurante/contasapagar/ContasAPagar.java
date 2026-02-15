package com.lucas.api_restaurante.contasapagar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.fornecedor.Fornecedor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contas_a_pagar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContasAPagar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "valor_a_pagar")
    private BigDecimal valorAPagar;
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private EstadoPagamento estado;
    private String descricao;

    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "id_fornecedor")
    private Fornecedor fornecedor;
}
