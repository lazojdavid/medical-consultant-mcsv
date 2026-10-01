package medical_consult.citas.messaging.event;

import java.util.UUID;

public record AppointmentEvent(
        UUID eventId,
        String eventType,
        String aggregateId,
        String aggregateType,
        String occurredAt,
        int version,
        String source,
        String correlationId,
        Payload payload) {

    public record Payload(
            Long appointmentId,
            Long patientId,
            Long doctorId,
            String startsAt,
            String endsAt,
            String reason,
            String cancellationReason) {
    }
}