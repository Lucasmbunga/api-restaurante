package com.lucas.api_restaurante.pedidosalao;

import com.lucas.api_restaurante.itempedido.ItemPedidoRequestDto;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoSalaoRequestDto(
        @NotEmpty List<ItemPedidoRequestDto> itensDoPedido,
        Long idCliente,
        Long idMesa,
        String descricao
) {
}
