package medical_consult.citas.dto.response;

import java.time.LocalDateTime;

import medical_consult.citas.models.AppointmentStatus;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long doctorId,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        AppointmentStatus status,
        String reason,
        String cancellationReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}