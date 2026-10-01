package medical_consult.citas.messaging;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String APPOINTMENT_SCHEDULED = "appointment.scheduled";
    public static final String APPOINTMENT_RESCHEDULED = "appointment.rescheduled";
    public static final String APPOINTMENT_CANCELLED = "appointment.cancelled";
}