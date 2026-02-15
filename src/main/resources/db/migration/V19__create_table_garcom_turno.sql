CREATE TABLE garcom_turno(
    id_garcom BIGINT NOT NULL,
    id_turno BIGINT NOT NULL,
    PRIMARY KEY(id_garcom,id_turno),
    FOREIGN KEY(id_garcom) REFERENCES garcom(id),
    FOREIGN KEY(id_turno) REFERENCES turno(id)
);