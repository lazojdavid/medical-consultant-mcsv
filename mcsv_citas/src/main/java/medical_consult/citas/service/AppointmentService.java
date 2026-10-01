package medical_consult.citas.service;

import java.util.List;

import medical_consult.citas.dto.request.CancelAppointmentRequest;
import medical_consult.citas.dto.request.RescheduleAppointmentRequest;
import medical_consult.citas.dto.request.ScheduleAppointmentRequest;
import medical_consult.citas.dto.response.AppointmentResponse;

public interface AppointmentService {

    AppointmentResponse schedule(ScheduleAppointmentRequest request, String correlationId);

    AppointmentResponse reschedule(Long id, RescheduleAppointmentRequest request, String correlationId);

    AppointmentResponse cancel(Long id, CancelAppointmentRequest request, String correlationId);

    AppointmentResponse findById(Long id);

    List<AppointmentResponse> findByPatient(Long patientId);

    List<AppointmentResponse> findByDoctor(Long doctorId);
}