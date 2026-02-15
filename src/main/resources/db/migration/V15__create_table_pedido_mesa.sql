CREATE TABLE pedido_mesa(
    id_pedido BIGINT NOT NULL,
    id_mesa BIGINT NOT NULL,
    PRIMARY KEY(id_pedido,id_mesa),
    FOREIGN KEY(id_pedido) REFERENCES pedido_salao(id),
    FOREIGN KEY(id_mesa) REFERENCES mesa(id)
);