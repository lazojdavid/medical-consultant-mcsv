CREATE SCHEMA IF NOT EXISTS citas;

CREATE TABLE IF NOT EXISTS citas.availabilities (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_availabilities_time_range CHECK (start_time < end_time)
);

CREATE INDEX IF NOT EXISTS ix_availabilities_doctor_day
    ON citas.availabilities (doctor_id, day_of_week, active);

CREATE TABLE IF NOT EXISTS citas.appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(500),
    cancellation_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_appointments_time_range CHECK (starts_at < ends_at)
);

CREATE INDEX IF NOT EXISTS ix_appointments_doctor_schedule
    ON citas.appointments (doctor_id, starts_at, ends_at, status);
CREATE INDEX IF NOT EXISTS ix_appointments_patient_schedule
    ON citas.appointments (patient_id, starts_at, ends_at, status);