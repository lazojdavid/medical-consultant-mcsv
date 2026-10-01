package medical_consult.citas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medical_consult.citas.commons.BadRequestException;
import medical_consult.citas.commons.ConflictException;
import medical_consult.citas.commons.ResourceNotFoundException;
import medical_consult.citas.dto.request.CreateAvailabilityRequest;
import medical_consult.citas.dto.request.UpdateAvailabilityRequest;
import medical_consult.citas.dto.response.AvailabilityResponse;
import medical_consult.citas.mappers.AvailabilityMapper;
import medical_consult.citas.models.AvailabilityEntity;
import medical_consult.citas.repository.AvailabilityRepository;
import medical_consult.citas.service.AvailabilityService;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityMapper availabilityMapper;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
            AvailabilityMapper availabilityMapper) {
        this.availabilityRepository = availabilityRepository;
        this.availabilityMapper = availabilityMapper;
    }

    @Override
    @Transactional
    public AvailabilityResponse create(CreateAvailabilityRequest request) {
        validateWindow(request.startTime(), request.endTime());
        ensureNoOverlap(request.doctorId(), request.dayOfWeek(), request.startTime(), request.endTime(), null);
        return availabilityMapper.toDto(availabilityRepository.save(availabilityMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public AvailabilityResponse update(Long id, UpdateAvailabilityRequest request) {
        AvailabilityEntity availability = getById(id);
        validateWindow(request.startTime(), request.endTime());
        if (request.active()) {
            ensureNoOverlap(availability.getDoctorId(), request.dayOfWeek(),
                    request.startTime(), request.endTime(), id);
        }
        availability.setDayOfWeek(request.dayOfWeek());
        availability.setStartTime(request.startTime());
        availability.setEndTime(request.endTime());
        availability.setActive(request.active());
        return availabilityMapper.toDto(availabilityRepository.save(availability));
    }

    @Override
    @Transactional
    public AvailabilityResponse deactivate(Long id) {
        AvailabilityEntity availability = getById(id);
        availability.setActive(false);
        return availabilityMapper.toDto(availabilityRepository.save(availability));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> findByDoctor(Long doctorId) {
        if (doctorId == null || doctorId <= 0) {
            throw new BadRequestException("Doctor ID must be positive.");
        }
        return availabilityRepository.findByDoctorIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(doctorId).stream()
                .map(availabilityMapper::toDto).toList();
    }

    private AvailabilityEntity getById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability " + id + " was not found."));
    }

    private void validateWindow(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Availability start time must be before its end time.");
        }
    }

    private void ensureNoOverlap(Long doctorId, Integer dayOfWeek,
            java.time.LocalTime startTime, java.time.LocalTime endTime, Long excludedId) {
        if (availabilityRepository.hasAvailabilityConflict(doctorId, dayOfWeek, startTime, endTime, excludedId)) {
            throw new ConflictException("Availability windows for the same doctor cannot overlap.");
        }
    }
}