CREATE TABLE IF NOT EXISTS especialidades (
    id          VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activa      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS medicos (
    id            VARCHAR(36)  PRIMARY KEY,
    usuario_id    VARCHAR(36)  NOT NULL UNIQUE REFERENCES usuarios(id),
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    dni           CHAR(8)      NOT NULL UNIQUE,
    codigo_cmp    VARCHAR(20)  UNIQUE,
    telefono      VARCHAR(15),
    especialidad_id VARCHAR(36) REFERENCES especialidades(id),
    estado        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    created_at    TIMESTAMPTZ  DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  DEFAULT NOW(),
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS horarios_medico (
    id               VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    medico_id        VARCHAR(36) NOT NULL REFERENCES medicos(id),
    dia_semana       VARCHAR(20) NOT NULL,
    hora_inicio      TIME        NOT NULL,
    hora_fin         TIME        NOT NULL,
    duracion_cita_min INT        NOT NULL DEFAULT 30,
    activo           BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ DEFAULT NOW(),
    updated_at       TIMESTAMPTZ DEFAULT NOW(),
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    UNIQUE(medico_id, dia_semana)
);

CREATE INDEX IF NOT EXISTS idx_medicos_especialidad ON medicos(especialidad_id);
CREATE INDEX IF NOT EXISTS idx_horarios_medico      ON horarios_medico(medico_id);
