CREATE TABLE pedido(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hora TIME NOT NULL,
    data DATE NOT NULL,
    valor_total DECIMAL,
    descricao VARCHAR(255),
    estado ENUM('ABERTO','EM_PREPARO','CANCELADO','ENTREGUE','FECHADO'),
    id_cliente BIGINT NOT NULL,
    id_garcom BIGINT NOT NULL,
    id_turno BIGINT NOT NULL,
    FOREIGN KEY(id_cliente)
    REFERENCES cliente(id),
    FOREIGN KEY(id_garcom) REFERENCES garcom(id),
    FOREIGN KEY(id_turno) REFERENCES turno(id)
);