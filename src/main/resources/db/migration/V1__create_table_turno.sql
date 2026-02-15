CREATE TABLE turno(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    hora_abertura TIME NOT NULL,
    hora_fecho TIME,
    total_entrada DECIMAL,
    total_saida DECIMAL,
    saldo_inicial DECIMAL,
    saldo_final DECIMAL

);
