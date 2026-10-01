package medical_consult.citas.service;

import java.util.List;

import medical_consult.citas.dto.request.CreateAvailabilityRequest;
import medical_consult.citas.dto.request.UpdateAvailabilityRequest;
import medical_consult.citas.dto.response.AvailabilityResponse;

public interface AvailabilityService {

    AvailabilityResponse create(CreateAvailabilityRequest request);

    AvailabilityResponse update(Long id, UpdateAvailabilityRequest request);

    AvailabilityResponse deactivate(Long id);

    List<AvailabilityResponse> findByDoctor(Long doctorId);
}