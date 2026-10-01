-- Catálogo de estados de una cita
CREATE TABLE IF NOT EXISTS citas.estados_cita (
    estado_cita_id BIGSERIAL PRIMARY KEY,
    description VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO citas.estados_cita (description)
VALUES
    ('RESERVADA'),
    ('CONFIRMADA'),
    ('ATENDIDA'),
    ('CANCELADA'),
    ('NO_ASISTIO')
ON CONFLICT (description) DO NOTHING;


-- Horarios semanales de los médicos
CREATE TABLE IF NOT EXISTS citas.horarios_medicos (
    horario_id BIGSERIAL PRIMARY KEY,
    medico_user_id BIGINT NOT NULL,
    day_of_week SMALLINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INTEGER NOT NULL DEFAULT 30,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_horarios_medico
        FOREIGN KEY (medico_user_id)
        REFERENCES data.users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_horarios_day
        CHECK (day_of_week BETWEEN 1 AND 7),

    CONSTRAINT chk_horarios_time
        CHECK (end_time > start_time),

    CONSTRAINT chk_horarios_duration
        CHECK (slot_duration_minutes > 0)
);


-- Excepciones de horario: días no laborables o turnos especiales
CREATE TABLE IF NOT EXISTS citas.excepciones_horario (
    excepcion_id BIGSERIAL PRIMARY KEY,
    medico_user_id BIGINT NOT NULL,
    exception_date DATE NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT FALSE,
    start_time TIME,
    end_time TIME,
    reason VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_excepciones_medico
        FOREIGN KEY (medico_user_id)
        REFERENCES data.users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_excepciones_time
        CHECK (
            (start_time IS NULL AND end_time IS NULL)
            OR
            (start_time IS NOT NULL AND end_time > start_time)
        )
);


-- Registro de citas
CREATE TABLE IF NOT EXISTS citas.citas (
    cita_id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    medico_user_id BIGINT NOT NULL,
    estado_cita_id BIGINT NOT NULL,
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    reason TEXT,
    notes TEXT,
    created_by BIGINT NOT NULL,
    cancelled_by BIGINT,
    cancelled_at TIMESTAMPTZ,
    cancellation_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_citas_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES pacientes.pacientes(paciente_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_citas_medico
        FOREIGN KEY (medico_user_id)
        REFERENCES data.users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_citas_estado
        FOREIGN KEY (estado_cita_id)
        REFERENCES citas.estados_cita(estado_cita_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_citas_created_by
        FOREIGN KEY (created_by)
        REFERENCES data.users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_citas_cancelled_by
        FOREIGN KEY (cancelled_by)
        REFERENCES data.users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_citas_time
        CHECK (end_at > start_at),

    CONSTRAINT chk_citas_cancelled
        CHECK (
            (cancelled_at IS NULL AND cancelled_by IS NULL)
            OR
            (cancelled_at IS NOT NULL AND cancelled_by IS NOT NULL)
        )
);


-- Evitar solapamientos de citas activas del mismo médico
CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE citas.citas
ADD CONSTRAINT ex_citas_medico_horario
EXCLUDE USING gist (
    medico_user_id WITH =,
    tstzrange(start_at, end_at, '[)') WITH &&
)
WHERE (cancelled_at IS NULL);


-- Índices
CREATE INDEX IF NOT EXISTS idx_citas_paciente
    ON citas.citas(paciente_id);

CREATE INDEX IF NOT EXISTS idx_citas_medico_fecha
    ON citas.citas(medico_user_id, start_at);

CREATE INDEX IF NOT EXISTS idx_citas_estado
    ON citas.citas(estado_cita_id);

CREATE INDEX IF NOT EXISTS idx_horarios_medico
    ON citas.horarios_medicos(medico_user_id);

CREATE INDEX IF NOT EXISTS idx_excepciones_medico_fecha
    ON citas.excepciones_horario(medico_user_id, exception_date);

