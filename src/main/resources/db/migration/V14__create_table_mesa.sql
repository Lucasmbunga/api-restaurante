CREATE TABLE mesa(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_mesa INT NOT NULL,
    capacidade INT,
    esta_ocupada BOOLEAN NOT NULL
);