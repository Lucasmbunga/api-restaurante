CREATE TABLE pedido_salao(
  id BIGINT PRIMARY KEY,
  CONSTRAINT fk_pedido_salao_pedido
  FOREIGN KEY(id) REFERENCES pedido(id)
  ON DELETE CASCADE
);