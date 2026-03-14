CREATE TABLE IF NOT EXISTS mesa(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_mesa INT NOT NULL UNIQUE,
    capacidade INT,
    esta_ocupada BOOLEAN NOT NULL
);