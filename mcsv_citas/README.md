# mcsv_citas

Appointment scheduling microservice for `medical_system`.

## Capabilities

- Schedule and reschedule appointments.
- Cancel appointments with a required reason.
- Create, update, deactivate, and view recurring weekly doctor availability.
- Reject appointments outside availability and overlapping appointments for either the doctor or patient.
- Publish appointment lifecycle events to Kafka after the database transaction commits. A notification delivery consumer (email/SMS/push) is intentionally not included until its provider and delivery contract are selected.
- Validate JWTs issued by `mcsv_auth` using HS256, the shared issuer, and the `roles` claim.

The service owns the `citas` schema. `patientId` and `doctorId` are external identifiers; this service does not create cross-microservice foreign keys or query another service's database.

## Requirements and configuration

- Java 25
- Maven (or a Maven wrapper)
- PostgreSQL database `medical_system` with a database user permitted to create/use the `citas` schema
- Kafka broker when scheduling, rescheduling, or cancellation notifications are required
- `AUTH_JWT_SECRET` must match the HS256 secret configured by `mcsv_auth`; `AUTH_JWT_ISSUER` defaults to `mcsv-auth`

Environment overrides:

| Variable | Default |
| --- | --- |
| `SERVER_PORT` | `8082` |
| `DB_URL` | `jdbc:postgresql://localhost:5432/medical_system` |
| `DB_USERNAME` | `admin` |
| `DB_PASSWORD` | `admin` |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `AUTH_JWT_SECRET` | Required |
| `AUTH_JWT_ISSUER` | `mcsv-auth` |

Hibernate updates the `citas` schema at startup. The DDL reference is in `src/main/resources/query.sql`.

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/appointments` | Schedule appointment |
| `PUT` | `/api/appointments/{id}/reschedule` | Reschedule appointment |
| `POST` | `/api/appointments/{id}/cancel` | Cancel appointment |
| `GET` | `/api/appointments/{id}` | Get appointment |
| `GET` | `/api/appointments/patient/{patientId}` | List patient appointments (staff roles only) |
| `GET` | `/api/appointments/doctor/{doctorId}` | List doctor appointments |
| `POST` | `/api/availabilities` | Create recurring weekly availability |
| `PUT` | `/api/availabilities/{id}` | Update availability |
| `DELETE` | `/api/availabilities/{id}` | Deactivate availability |
| `GET` | `/api/availabilities/doctor/{doctorId}` | List active availability |

Appointment mutations and patient appointment reads require `ADMINISTRADOR`, `MEDICO`, or `RECEPCIONISTA`. Availability reads also allow `PACIENTE`; availability changes require `ADMINISTRADOR` or `MEDICO`.

Weekly availability uses ISO weekday numbers (Monday=1 through Sunday=7). An appointment must start in the future, stay within a single day, fit one active availability window, and not overlap another active appointment belonging to the same doctor or patient. All times are local date-times without a timezone; deployments should agree on the clinic timezone.

## Notification events

Kafka messages use the appointment ID as the key and are sent after commit:

- `appointment.scheduled`
- `appointment.rescheduled`
- `appointment.cancelled`

Each event includes `eventId`, `eventType`, `aggregateId`, `aggregateType`, `occurredAt`, `version`, `source`, `correlationId`, and a consumer-focused payload. A notification service can consume these topics and deliver email, SMS, or push without blocking the appointment API.

## Run and test

```powershell
$env:AUTH_JWT_SECRET = '<same secret configured in mcsv_auth>'
mvn spring-boot:run
mvn test
```

Use `testhttp/appointments.http` and `testhttp/availability.http` for request examples. Swagger UI is available at `/swagger-ui.html` and health at `/actuator/health`.