package medical_consult.citas.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import medical_consult.citas.models.AppointmentEntity;
import medical_consult.citas.models.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select a from AppointmentEntity a where a.id = :id")
        Optional<AppointmentEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select count(a) > 0 from AppointmentEntity a
            where (a.doctorId = :doctorId or a.patientId = :patientId)
              and a.status <> :cancelledStatus
              and a.startsAt < :endsAt
              and a.endsAt > :startsAt
              and (:excludedId is null or a.id <> :excludedId)
            """)
    boolean hasScheduleConflict(
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt,
            @Param("excludedId") Long excludedId,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus);

    Optional<AppointmentEntity> findByIdAndStatus(Long id, AppointmentStatus status);

    List<AppointmentEntity> findByPatientIdOrderByStartsAtAsc(Long patientId);

    List<AppointmentEntity> findByDoctorIdOrderByStartsAtAsc(Long doctorId);
}