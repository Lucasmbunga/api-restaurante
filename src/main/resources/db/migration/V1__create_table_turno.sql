CREATE TABLE IF NOT EXISTS turno(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    hora_abertura TIME NOT NULL,
    hora_fecho TIME,
   status ENUM('ABERTO','FECHADO') NOT NULL

);
