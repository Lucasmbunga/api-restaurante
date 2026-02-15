
CREATE TABLE gestor(
  id BIGINT PRIMARY KEY ,

  CONSTRAINT fk_gestor_usuario
  FOREIGN KEY(id)
  REFERENCES usuario(id)
  ON DELETE CASCADE

);