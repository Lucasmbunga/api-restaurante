package com.lucas.api_restaurante.pagamento;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.pedido.Pedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private BigDecimal valorPago;
    @NotNull
    private LocalDateTime dataPagamento;
    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
}
