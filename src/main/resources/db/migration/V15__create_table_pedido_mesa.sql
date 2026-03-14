CREATE TABLE IF NOT EXISTS pedido_mesa(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_pedido BIGINT NOT NULL,
    id_mesa BIGINT NOT NULL,
    FOREIGN KEY(id_pedido) REFERENCES pedido_salao(id),
    FOREIGN KEY(id_mesa) REFERENCES mesa(id)
);