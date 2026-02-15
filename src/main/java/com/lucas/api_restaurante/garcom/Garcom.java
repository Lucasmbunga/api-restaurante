package com.lucas.api_restaurante.garcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.turno.Turno;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "garcom")
@Getter
@Setter
public class Garcom extends Usuario {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoGarcom cargo;
}
