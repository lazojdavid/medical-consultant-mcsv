package medical_consult.citas.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record RescheduleAppointmentRequest(
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt) {
}