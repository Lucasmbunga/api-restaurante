CREATE TABLE IF NOT EXISTS entrada_caixa(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data DATETIME NOT NULL,
    descricao VARCHAR(255),
    id_pagamento BIGINT NOT NULL,
    id_turno BIGINT NOT NULL,
    id_caixa BIGINT NOT NULL,
    FOREIGN KEY(id_pagamento) REFERENCES pagamento(id),
    FOREIGN KEY(id_turno) REFERENCES turno(id),
    FOREIGN KEY(id_caixa) REFERENCES caixa(id)
);