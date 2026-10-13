-- =====================================================================
-- V7 - Secuencia del numero de historia clinica.
--
-- Va en una migracion aparte y no dentro de V2 porque V2 ya esta aplicada,
-- y una migracion aplicada nunca se modifica: Flyway guarda su checksum y
-- cambiarla haria fallar el arranque con "Migration checksum mismatch"
-- (instruccion M3).
--
-- Hace falta una secuencia dedicada en lugar de derivar el numero del id de
-- la fila: el id es un detalle interno de la base, mientras que el numero de
-- historia es un dato que el personal dicta por telefono y escribe en papel.
-- Tenerlos separados permite que el formato cambie sin tocar las claves.
-- =====================================================================

CREATE SEQUENCE seq_numero_historia
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    CACHE 1;

COMMENT ON SEQUENCE seq_numero_historia IS
    'Correlativo del numero de historia clinica. Lo consume PacienteServiceImpl para formar HC-000001.';
