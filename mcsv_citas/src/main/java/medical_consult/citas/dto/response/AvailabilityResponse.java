package medical_consult.citas.dto.response;

import java.time.LocalTime;

public record AvailabilityResponse(
        Long id,
        Long doctorId,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        Boolean active) {
}