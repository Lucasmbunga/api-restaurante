CREATE TABLE IF NOT EXISTS item_cardapio(
    id_produto BIGINT NOT NULL,
    id_cardapio BIGINT NOT NULL,
    PRIMARY KEY(id_produto,id_cardapio),
    FOREIGN KEY(id_produto) REFERENCES produto(id),
    FOREIGN KEY(id_cardapio) REFERENCES cardapio(id)

);