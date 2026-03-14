
CREATE TABLE IF NOT EXISTS cliente(
    id BIGINT PRIMARY KEY,
    nif VARCHAR(50) UNIQUE,
    CONSTRAINT fk_cliente_usuario
    FOREIGN KEY(id)
    REFERENCES usuario(id)
    ON DELETE CASCADE
);