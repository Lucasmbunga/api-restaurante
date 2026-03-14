CREATE TABLE IF NOT EXISTS item_pedido(
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       quantidade INT NOT NULL,
       preco_unitario DECIMAL NOT NULL,
       preco_total DECIMAL NOT NULL,
       observacao VARCHAR(255),
       id_produto BIGINT NOT NULL ,
       id_pedido BIGINT NOT NULL,
       FOREIGN KEY(id_produto) REFERENCES produto(id),
       FOREIGN KEY(id_pedido) REFERENCES pedido(id)
);