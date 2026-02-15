package com.lucas.api_restaurante.endereco;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucas.api_restaurante.cliente.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cidade;
    @NotNull
    private String bairro;
    private Integer zona;
    private String rua;

    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente",nullable = false)
    private Cliente cliente;

    public Endereco(String cidade, String bairro, Integer zona, String rua) {
        this.cidade = cidade;
        this.bairro = bairro;
        this.zona = zona;
        this.rua = rua;
    }
}
