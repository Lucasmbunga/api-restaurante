package com.lucas.api_restaurante.turno;

import com.lucas.api_restaurante.garcom.Garcom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "turno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate data;

    @NotNull
    @Column(name = "hora_abertura")
    private LocalTime horaAbertura;

    @Column(name = "hora_fecho")
    private LocalTime horaFecho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTurno status;
}
