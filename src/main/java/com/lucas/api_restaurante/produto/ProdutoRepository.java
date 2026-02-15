package com.lucas.api_restaurante.produto;

import com.lucas.api_restaurante.categoria.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Produto> findByTipoProduto(TipoProduto tipoProduto, Pageable pageable);
    Page<Produto> findByCategoria(Categoria categoria, Pageable pageable);
}
