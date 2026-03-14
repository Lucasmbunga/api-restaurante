
CREATE TABLE IF NOT EXISTS produto(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    preco_compra DECIMAL NOT NULL,
    preco_venda DECIMAL,
    id_categoria BIGINT NOT NULL,
    tipo_produto ENUM('PRATO','BEBIDA','SOBREMESA','ENTRADA'),
    FOREIGN KEY(id_categoria) REFERENCES categoria(id)
);