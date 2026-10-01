package medical_consult.citas.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import medical_consult.citas.messaging.KafkaTopics;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic appointmentScheduledTopic() {
        return TopicBuilder.name(KafkaTopics.APPOINTMENT_SCHEDULED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic appointmentRescheduledTopic() {
        return TopicBuilder.name(KafkaTopics.APPOINTMENT_RESCHEDULED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic appointmentCancelledTopic() {
        return TopicBuilder.name(KafkaTopics.APPOINTMENT_CANCELLED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}