package com.lucas.api_restaurante.mesa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer numeroMesa;
    private Integer capacidade;

    private Boolean estaOcupada;

}
