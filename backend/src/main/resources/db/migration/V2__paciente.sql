CREATE TABLE IF NOT EXISTS pacientes (
    id               VARCHAR(36)  PRIMARY KEY,
    usuario_id       VARCHAR(36)  NOT NULL UNIQUE REFERENCES usuarios(id),
    dni              CHAR(8)      NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    fecha_nacimiento DATE,
    sexo             VARCHAR(20),
    telefono         VARCHAR(15),
    direccion        TEXT,
    tipo_sangre      VARCHAR(20),
    alergias         TEXT,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ  DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  DEFAULT NOW(),
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_pacientes_usuario ON pacientes(usuario_id);
CREATE INDEX IF NOT EXISTS idx_pacientes_dni     ON pacientes(dni);
