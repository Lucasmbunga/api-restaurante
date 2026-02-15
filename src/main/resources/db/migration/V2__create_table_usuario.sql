CREATE TABLE usuario(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100)  UNIQUE,
    senha VARCHAR(255),
    telefone VARCHAR(20),
    tipo_usuario ENUM('GESTOR','GARCOM','CLIENTE')
);