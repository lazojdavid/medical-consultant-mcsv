package medical_consult.citas.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medical_consult.citas.commons.BadRequestException;
import medical_consult.citas.commons.ConflictException;
import medical_consult.citas.commons.ResourceNotFoundException;
import medical_consult.citas.dto.request.CancelAppointmentRequest;
import medical_consult.citas.dto.request.RescheduleAppointmentRequest;
import medical_consult.citas.dto.request.ScheduleAppointmentRequest;
import medical_consult.citas.dto.response.AppointmentResponse;
import medical_consult.citas.mappers.AppointmentMapper;
import medical_consult.citas.messaging.KafkaTopics;
import medical_consult.citas.messaging.producer.AppointmentEventProducer;
import medical_consult.citas.models.AppointmentEntity;
import medical_consult.citas.models.AppointmentStatus;
import medical_consult.citas.models.AvailabilityEntity;
import medical_consult.citas.repository.AppointmentRepository;
import medical_consult.citas.repository.AvailabilityRepository;
import medical_consult.citas.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentEventProducer eventProducer;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
            AvailabilityRepository availabilityRepository,
            AppointmentMapper appointmentMapper,
            AppointmentEventProducer eventProducer) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentMapper = appointmentMapper;
        this.eventProducer = eventProducer;
    }

    @Override
    @Transactional
    public AppointmentResponse schedule(ScheduleAppointmentRequest request, String correlationId) {
        validateRange(request.startsAt(), request.endsAt());
        validatePatientAndDoctorIds(request.patientId(), request.doctorId());
        lockAndValidateAvailability(request.doctorId(), request.startsAt(), request.endsAt());
        ensureNoConflict(request.doctorId(), request.patientId(), request.startsAt(), request.endsAt(), null);

        AppointmentResponse response = appointmentMapper.toDto(appointmentRepository.save(appointmentMapper.toEntity(request)));
        eventProducer.publish(KafkaTopics.APPOINTMENT_SCHEDULED, response, correlationId);
        return response;
    }

    @Override
    @Transactional
    public AppointmentResponse reschedule(Long id, RescheduleAppointmentRequest request, String correlationId) {
        AppointmentEntity appointment = getForUpdate(id);
        ensureScheduled(appointment);
        validateRange(request.startsAt(), request.endsAt());
        lockAndValidateAvailability(appointment.getDoctorId(), request.startsAt(), request.endsAt());
        ensureNoConflict(appointment.getDoctorId(), appointment.getPatientId(),
                request.startsAt(), request.endsAt(), appointment.getId());

        appointment.setStartsAt(request.startsAt());
        appointment.setEndsAt(request.endsAt());
        AppointmentResponse response = appointmentMapper.toDto(appointmentRepository.save(appointment));
        eventProducer.publish(KafkaTopics.APPOINTMENT_RESCHEDULED, response, correlationId);
        return response;
    }

    @Override
    @Transactional
    public AppointmentResponse cancel(Long id, CancelAppointmentRequest request, String correlationId) {
        AppointmentEntity appointment = getForUpdate(id);
        ensureScheduled(appointment);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(request.reason().trim());
        AppointmentResponse response = appointmentMapper.toDto(appointmentRepository.save(appointment));
        eventProducer.publish(KafkaTopics.APPOINTMENT_CANCELLED, response, correlationId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return appointmentMapper.toDto(appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " was not found.")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByStartsAtAsc(patientId).stream()
                .map(appointmentMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByStartsAtAsc(doctorId).stream()
                .map(appointmentMapper::toDto).toList();
    }

    private AppointmentEntity getForUpdate(Long id) {
        return appointmentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment " + id + " was not found."));
    }

    private void ensureScheduled(AppointmentEntity appointment) {
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ConflictException("Only scheduled appointments can be changed.");
        }
    }

    private void validateRange(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (!startsAt.isBefore(endsAt)) {
            throw new BadRequestException("Appointment start time must be before its end time.");
        }
        if (!startsAt.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Appointments must be scheduled in the future.");
        }
        if (!startsAt.toLocalDate().equals(endsAt.toLocalDate())) {
            throw new BadRequestException("Appointments cannot span multiple days.");
        }
    }

    private void validatePatientAndDoctorIds(Long patientId, Long doctorId) {
        if (patientId == null || patientId <= 0 || doctorId == null || doctorId <= 0) {
            throw new BadRequestException("Patient and doctor IDs must be positive.");
        }
    }

    private void lockAndValidateAvailability(Long doctorId, LocalDateTime startsAt, LocalDateTime endsAt) {
        int dayOfWeek = startsAt.getDayOfWeek().getValue();
        List<AvailabilityEntity> windows = availabilityRepository.lockActiveByDoctorAndDay(doctorId, dayOfWeek);
        boolean fitsWindow = windows.stream().anyMatch(window ->
                !startsAt.toLocalTime().isBefore(window.getStartTime())
                        && !endsAt.toLocalTime().isAfter(window.getEndTime()));
        if (!fitsWindow) {
            throw new ConflictException("The appointment is outside the doctor's availability.");
        }
    }

    private void ensureNoConflict(Long doctorId, Long patientId, LocalDateTime startsAt,
            LocalDateTime endsAt, Long excludedId) {
        boolean hasConflict = appointmentRepository.hasScheduleConflict(
                doctorId, patientId, startsAt, endsAt, excludedId, AppointmentStatus.CANCELLED);
        if (hasConflict) {
            throw new ConflictException("The doctor or patient already has an appointment during that time.");
        }
    }
}