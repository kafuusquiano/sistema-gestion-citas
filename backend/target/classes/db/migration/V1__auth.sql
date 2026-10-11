CREATE EXTENSION IF NOT EXISTS "pgcrypto";

SET timezone = 'America/Lima';

CREATE TABLE IF NOT EXISTS usuarios (
    id           VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    email        VARCHAR(255) NOT NULL UNIQUE,
    dni          CHAR(8)      NOT NULL UNIQUE,
    nombre       VARCHAR(100) NOT NULL,
    apellido     VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol          VARCHAR(20)  NOT NULL,
    estado       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    created_at   TIMESTAMPTZ  DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  DEFAULT NOW(),
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    token       TEXT         NOT NULL UNIQUE,
    usuario_id  VARCHAR(36)  NOT NULL REFERENCES usuarios(id),
    expira_en   TIMESTAMPTZ  NOT NULL,
    revocado    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  DEFAULT NOW(),
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_usuario ON refresh_tokens(usuario_id);
