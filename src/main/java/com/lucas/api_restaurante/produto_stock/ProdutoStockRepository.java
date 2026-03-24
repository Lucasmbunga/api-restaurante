package com.lucas.api_restaurante.produto_stock;

import com.lucas.api_restaurante.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoStockRepository extends JpaRepository<ProdutoStock,Long> {
    Optional<ProdutoStock> findByProduto(Produto produto);
}
