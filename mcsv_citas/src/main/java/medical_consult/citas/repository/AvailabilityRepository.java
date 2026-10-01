package medical_consult.citas.repository;

import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import medical_consult.citas.models.AvailabilityEntity;

public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {

    List<AvailabilityEntity> findByDoctorIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(Long doctorId);

    List<AvailabilityEntity> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, Integer dayOfWeek);

    @Query("""
            select count(a) > 0 from AvailabilityEntity a
            where a.doctorId = :doctorId and a.dayOfWeek = :dayOfWeek and a.active = true
              and a.startTime < :endTime and a.endTime > :startTime
              and (:excludedId is null or a.id <> :excludedId)
            """)
    boolean hasAvailabilityConflict(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime,
            @Param("excludedId") Long excludedId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a from AvailabilityEntity a
            where a.doctorId = :doctorId and a.dayOfWeek = :dayOfWeek and a.active = true
            """)
    List<AvailabilityEntity> lockActiveByDoctorAndDay(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek);
}