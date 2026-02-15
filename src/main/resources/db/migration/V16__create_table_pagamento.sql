CREATE TABLE pagamento(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_pedido BIGINT NOT NULL,
    valor_pago DECIMAL NOT NULL,
    data_pagamento DATETIME NOT NULL,
    metodo_pagamento ENUM('DINHEIRO','MULTICAIXA','TRANSFERENCIA','TPA','CREDITO') NOT NULL,
    FOREIGN KEY(id_pedido) REFERENCES pedido(id)
);