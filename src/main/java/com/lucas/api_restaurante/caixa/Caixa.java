package com.lucas.api_restaurante.caixa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.entradacaixa.EntradaCaixa;
import com.lucas.api_restaurante.saidacaixa.SaidaCaixa;
import com.lucas.api_restaurante.turno.Turno;
import jakarta.persistence.*;
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
public class Caixa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal faturamentoLiquido=BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal valorInicial;

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<EntradaCaixa> entradas;

    @OneToMany(mappedBy = "caixa", cascade =  CascadeType.ALL,orphanRemoval = true)
    private List<SaidaCaixa> saidas;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", nullable = false)
    private Turno turno;

    public void darEntradaNaCaixa(EntradaCaixa entrada){
        this.entradas.add(entrada);
    }
    public void registrarSaida(SaidaCaixa saida){
        this.saidas.add(saida);
    }
    public BigDecimal getTotalEntradas(){
        if(this.entradas.isEmpty()){
            return BigDecimal.ZERO;
        }
        return this.entradas.stream()
                .map(entrada->entrada.getPagamento().getValorPago())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
