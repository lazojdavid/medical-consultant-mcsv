package medical_consult.citas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import medical_consult.citas.commons.ConflictException;
import medical_consult.citas.dto.request.CancelAppointmentRequest;
import medical_consult.citas.dto.request.ScheduleAppointmentRequest;
import medical_consult.citas.messaging.KafkaTopics;
import medical_consult.citas.messaging.producer.AppointmentEventProducer;
import medical_consult.citas.models.AppointmentEntity;
import medical_consult.citas.models.AppointmentStatus;
import medical_consult.citas.repository.AppointmentRepository;
import medical_consult.citas.repository.AvailabilityRepository;
import medical_consult.citas.mappers.AppointmentMapper;

class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AppointmentEventProducer eventProducer;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentServiceImpl(
                appointmentRepository, availabilityRepository, new AppointmentMapper(), eventProducer);
    }

    @Test
    void scheduleRejectsTimeOutsideAvailability() {
        LocalDateTime startsAt = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        when(availabilityRepository.lockActiveByDoctorAndDay(22L, startsAt.getDayOfWeek().getValue()))
                .thenReturn(java.util.List.of());

        assertThrows(ConflictException.class, () -> appointmentService.schedule(
                new ScheduleAppointmentRequest(11L, 22L, startsAt, startsAt.plusMinutes(30), "Checkup"), "corr-1"));

        verify(appointmentRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(eventProducer, never()).publish(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void cancelMarksAppointmentAndPublishesEvent() {
        AppointmentEntity entity = AppointmentEntity.builder()
                .id(9L)
                .patientId(11L)
                .doctorId(22L)
                .startsAt(LocalDateTime.now().plusDays(2))
                .endsAt(LocalDateTime.now().plusDays(2).plusMinutes(30))
                .status(AppointmentStatus.SCHEDULED)
                .build();
        when(appointmentRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(entity));
        when(appointmentRepository.save(entity)).thenReturn(entity);

        var response = appointmentService.cancel(9L, new CancelAppointmentRequest(" Patient request "), "corr-2");

        assertEquals(AppointmentStatus.CANCELLED, response.status());
        assertEquals("Patient request", response.cancellationReason());
        verify(eventProducer).publish(KafkaTopics.APPOINTMENT_CANCELLED, response, "corr-2");
    }
}