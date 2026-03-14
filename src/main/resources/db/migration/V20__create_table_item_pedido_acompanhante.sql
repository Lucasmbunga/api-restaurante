CREATE TABLE IF NOT EXISTS item_pedido_acompanhante(
   id_acompanhante BIGINT NOT NULL,
   id_item_pedido BIGINT NOT NULL,
   PRIMARY KEY(id_acompanhante,id_item_pedido),
   FOREIGN KEY(id_acompanhante) REFERENCES acompanhante(id),
   FOREIGN KEY(id_item_pedido) REFERENCES item_pedido(id)

);