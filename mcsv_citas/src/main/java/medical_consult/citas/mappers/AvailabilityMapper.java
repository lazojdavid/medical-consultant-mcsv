package medical_consult.citas.mappers;

import org.springframework.stereotype.Component;

import medical_consult.citas.dto.request.CreateAvailabilityRequest;
import medical_consult.citas.dto.response.AvailabilityResponse;
import medical_consult.citas.models.AvailabilityEntity;

@Component
public class AvailabilityMapper {

    public AvailabilityEntity toEntity(CreateAvailabilityRequest request) {
        return AvailabilityEntity.builder()
                .doctorId(request.doctorId())
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .active(true)
                .build();
    }

    public AvailabilityResponse toDto(AvailabilityEntity entity) {
        return new AvailabilityResponse(
                entity.getId(), entity.getDoctorId(), entity.getDayOfWeek(),
                entity.getStartTime(), entity.getEndTime(), entity.getActive());
    }
}