CREATE TABLE produto_stock(
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   id_produto BIGINT NOT NULL UNIQUE,
   quantidade_disponivel INT,
   FOREIGN KEY(id_produto) REFERENCES produto(id)
);