package medical_consult.citas.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import medical_consult.citas.messaging.KafkaTopics;
import medical_consult.citas.messaging.event.AppointmentEvent;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true")
public class AppointmentNotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AppointmentNotificationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(AppointmentEvent event) {
        String topic = switch (event.eventType()) {
            case KafkaTopics.APPOINTMENT_SCHEDULED -> KafkaTopics.APPOINTMENT_SCHEDULED;
            case KafkaTopics.APPOINTMENT_RESCHEDULED -> KafkaTopics.APPOINTMENT_RESCHEDULED;
            case KafkaTopics.APPOINTMENT_CANCELLED -> KafkaTopics.APPOINTMENT_CANCELLED;
            default -> null;
        };
        if (topic != null) {
            kafkaTemplate.send(topic, event.aggregateId(), event);
        }
    }
}