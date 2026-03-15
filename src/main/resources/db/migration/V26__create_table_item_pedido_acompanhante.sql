CREATE TABLE item_acompanhante(
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   id_item_pedido BIGINT NOT NULL,
   id_acompanhante BIGINT NOT NULL,
   FOREIGN KEY(id_item_pedido) REFERENCES item_pedido(id),
   FOREIGN KEY(id_acompanhante) REFERENCES acompanhante(id)

);