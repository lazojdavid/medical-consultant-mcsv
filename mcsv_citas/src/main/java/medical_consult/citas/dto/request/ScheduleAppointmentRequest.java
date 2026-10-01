package medical_consult.citas.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ScheduleAppointmentRequest(
        @NotNull @Positive Long patientId,
        @NotNull @Positive Long doctorId,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @Size(max = 500) String reason) {
}