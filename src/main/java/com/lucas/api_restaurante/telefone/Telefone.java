package com.lucas.api_restaurante.telefone;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "telefone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String numeroTelefone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(value = "idUsario",access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Telefone(@NotBlank String numeroTelefone,@NotNull Usuario usuario){
        this.numeroTelefone = numeroTelefone;
        this.usuario = usuario;
    }
}
