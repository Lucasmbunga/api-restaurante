
CREATE TABLE garcom(
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_garcom_usuario
    FOREIGN KEY(id)
    REFERENCES usuario(id)
    ON DELETE CASCADE

);