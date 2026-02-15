CREATE TABLE telefone(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_telefone VARCHAR(30),
    id_usuario BIGINT NOT NULL,
    FOREIGN KEY(id_usuario) REFERENCES usuario(id)
);