package com.lucas.api_restaurante.pedidodelivery;

import com.lucas.api_restaurante.itempedido.ItemPedido;
import com.lucas.api_restaurante.pedido.EstadoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PedidoDeliveryResponseDto(
                                        @NotNull Long id,
                                        @NotEmpty List<ItemPedido> itens,
                                        String nomeCliente,
                                        @NotBlank String nomeGarcom,
                                        @NotNull
                                        BigDecimal precoTotal,
                                        String descricao,
                                        @NotNull EstadoPedido estado) {
}
