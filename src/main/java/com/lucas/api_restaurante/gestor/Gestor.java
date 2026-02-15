package com.lucas.api_restaurante.gestor;

import com.lucas.api_restaurante.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gestor")
@Getter
@Setter
public class Gestor extends Usuario {

}
