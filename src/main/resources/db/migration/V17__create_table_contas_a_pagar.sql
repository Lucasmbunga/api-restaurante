CREATE TABLE contas_a_pagar(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    valor_a_pagar DECIMAL NOT NULL,
    data_vencimento DATE,
    estado ENUM('PENDENTE','PAGO','ATRASADO') NOT NULL,
    descricao VARCHAR(255),
    id_fornecedor BIGINT,
    FOREIGN KEY(id_fornecedor) REFERENCES fornecedor(id)
);