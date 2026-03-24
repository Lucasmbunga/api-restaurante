package com.lucas.api_restaurante.pedido_delivery;

import com.lucas.api_restaurante.item_pedido.ItemPedidoRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalTime;
import java.util.List;

public record PedidoDeliveryRequestDto(@NotEmpty List<ItemPedidoRequestDto> itensDoPedido,
                                       @NotBlank @Email String email,
                                       LocalTime previsaoChegada,
                                       String descricao) {
}
