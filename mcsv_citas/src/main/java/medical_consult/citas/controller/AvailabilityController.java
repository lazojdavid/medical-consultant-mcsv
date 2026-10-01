package medical_consult.citas.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medical_consult.citas.dto.request.CreateAvailabilityRequest;
import medical_consult.citas.dto.request.UpdateAvailabilityRequest;
import medical_consult.citas.dto.response.AvailabilityResponse;
import medical_consult.citas.service.AvailabilityService;

@RestController
@RequestMapping("/api/availabilities")
@Validated
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO')")
    public ResponseEntity<AvailabilityResponse> create(@Valid @RequestBody CreateAvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO')")
    public ResponseEntity<AvailabilityResponse> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO')")
    public ResponseEntity<AvailabilityResponse> deactivate(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(availabilityService.deactivate(id));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA', 'PACIENTE')")
    public ResponseEntity<List<AvailabilityResponse>> findByDoctor(@PathVariable @Positive Long doctorId) {
        return ResponseEntity.ok(availabilityService.findByDoctor(doctorId));
    }
}