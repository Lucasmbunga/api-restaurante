CREATE TABLE entrada_stock(
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   id_produto BIGINT NOT NULL,
   data DATE,
   hora TIME,
   quantidade INT,
   data_validade DATE,
   FOREIGN KEY(id_produto) REFERENCES produto(id)

);