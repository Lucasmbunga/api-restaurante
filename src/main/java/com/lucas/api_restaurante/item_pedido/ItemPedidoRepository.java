package com.lucas.api_restaurante.item_pedido;

import com.lucas.api_restaurante.pedido.Pedido;
import com.lucas.api_restaurante.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    ItemPedido findByProdutoAndPedido(Produto produto,Pedido pedido);
}
