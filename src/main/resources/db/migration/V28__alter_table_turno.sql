ALTER TABLE turno DROP COLUMN total_entrada;
ALTER TABLE turno DROP COLUMN total_saida;
ALTER TABLE turno DROP COLUMN saldo_final;
ALTER TABLE turno DROP COLUMN saldo_inicial;

ALTER TABLE turno ADD COLUMN status ENUM('ABERTO','FECHADO') NOT NULL;