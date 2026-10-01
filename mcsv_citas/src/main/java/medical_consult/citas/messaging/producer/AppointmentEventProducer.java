package medical_consult.citas.messaging.producer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import medical_consult.citas.dto.response.AppointmentResponse;
import medical_consult.citas.messaging.event.AppointmentEvent;

@Component
public class AppointmentEventProducer {

    private static final DateTimeFormatter EVENT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final ApplicationEventPublisher applicationEventPublisher;

    public AppointmentEventProducer(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(String eventType, AppointmentResponse appointment, String correlationId) {
        String normalizedCorrelationId;
        try {
            normalizedCorrelationId = UUID.fromString(correlationId).toString();
        } catch (IllegalArgumentException | NullPointerException exception) {
            normalizedCorrelationId = UUID.randomUUID().toString();
        }
        AppointmentEvent event = new AppointmentEvent(
                UUID.randomUUID(), eventType, appointment.id().toString(), "appointment",
                LocalDateTime.now().format(EVENT_DATE_FORMAT), 1, "mcsv-citas",
                normalizedCorrelationId,
                new AppointmentEvent.Payload(
                        appointment.id(), appointment.patientId(), appointment.doctorId(),
                        appointment.startsAt().format(EVENT_DATE_FORMAT), appointment.endsAt().format(EVENT_DATE_FORMAT),
                        appointment.reason(), appointment.cancellationReason()));
        applicationEventPublisher.publishEvent(event);
    }
}