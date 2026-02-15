package com.lucas.api_restaurante.cliente;

import com.lucas.api_restaurante.endereco.Endereco;
import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {
    @Column(unique = true)
    private String nif;

    @OneToMany(mappedBy = "cliente",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Endereco> endereco;
}
