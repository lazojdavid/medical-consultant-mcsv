package medical_consult.citas.mappers;

import org.springframework.stereotype.Component;

import medical_consult.citas.dto.request.ScheduleAppointmentRequest;
import medical_consult.citas.dto.response.AppointmentResponse;
import medical_consult.citas.models.AppointmentEntity;
import medical_consult.citas.models.AppointmentStatus;

@Component
public class AppointmentMapper {

    public AppointmentEntity toEntity(ScheduleAppointmentRequest request) {
        return AppointmentEntity.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .startsAt(request.startsAt())
                .endsAt(request.endsAt())
                .reason(request.reason())
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    public AppointmentResponse toDto(AppointmentEntity entity) {
        return new AppointmentResponse(
                entity.getId(), entity.getPatientId(), entity.getDoctorId(),
                entity.getStartsAt(), entity.getEndsAt(), entity.getStatus(),
                entity.getReason(), entity.getCancellationReason(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}