package com.lucas.api_restaurante.pagamento;

import com.lucas.api_restaurante.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    @Query(" FROM Pagamento as pag JOIN Pedido AS pe  ON pag.pedido.cliente.id=:idCliente")
    List<Pagamento> findAllByClienteId(@Param("idCliente") Long idCliente);

    Optional<Pagamento> findByPedido(Pedido pedido);
}
