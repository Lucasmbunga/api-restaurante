CREATE TABLE IF NOT EXISTS caixa(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    faturamento_liquido DECIMAL,
    valor_inicial DECIMAL NOT NULL,
    id_turno BIGINT NOT NULL,
    FOREIGN KEY(id_turno) REFERENCES turno(id)

);
