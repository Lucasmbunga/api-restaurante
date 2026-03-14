CREATE TABLE IF NOT EXISTS saida_caixa(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data DATETIME NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL NOT NULL,
    id_turno BIGINT NOT NULL,
    id_conta_a_pagar BIGINT,
    id_caixa BIGINT NOT NULL,
    FOREIGN KEY(id_turno) REFERENCES turno(id),
    FOREIGN KEY(id_conta_a_pagar) REFERENCES contas_a_pagar(id),
    FOREIGN KEY(id_caixa) REFERENCES caixa(id)

);