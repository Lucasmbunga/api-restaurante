CREATE TABLE saida_stock(
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   id_produto BIGINT NOT NULL,
   quantidade INT,
   data DATE,
   hora TIME,
   FOREIGN KEY(id_produto) REFERENCES produto(id)

);