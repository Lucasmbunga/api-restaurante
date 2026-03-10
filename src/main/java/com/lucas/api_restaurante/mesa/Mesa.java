package com.lucas.api_restaurante.mesa;

import com.lucas.api_restaurante.pedidosalao.PedidoSalao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pedido_mesa",
            joinColumns = @JoinColumn(name = "id_mesa"),
            inverseJoinColumns = @JoinColumn(name = "id_pedido")
    )
    private List<PedidoSalao> pedidos;

    public void adicionarPedido(PedidoSalao pedidoSalao) {
        this.pedidos.add(pedidoSalao);
    }
    public void esvaziar(PedidoSalao pedidoSalao) {
        this.pedidos.clear();
    }
    public void excluirPedido(PedidoSalao pedidoSalao) {
        this.pedidos.remove(pedidoSalao);
    }
}
