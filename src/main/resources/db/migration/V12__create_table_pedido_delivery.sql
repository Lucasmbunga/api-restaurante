CREATE TABLE IF NOT EXISTS pedido_delivery(
    id BIGINT PRIMARY KEY,
    id_endereco BIGINT,
    taxa_entrega DECIMAL,
    previsao_chegada TIME,
    CONSTRAINT fk_pedido_delivery_pedido
    FOREIGN KEY(id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY(id_endereco) REFERENCES endereco(id)
);