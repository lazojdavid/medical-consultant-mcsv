package medical_consult.citas.controller;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medical_consult.citas.dto.request.CancelAppointmentRequest;
import medical_consult.citas.dto.request.RescheduleAppointmentRequest;
import medical_consult.citas.dto.request.ScheduleAppointmentRequest;
import medical_consult.citas.dto.response.AppointmentResponse;
import medical_consult.citas.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<AppointmentResponse> schedule(
            @Valid @RequestBody ScheduleAppointmentRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.schedule(request, correlationId));
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable @Positive Long id,
            @Valid @RequestBody RescheduleAppointmentRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {
        return ResponseEntity.ok(appointmentService.reschedule(id, request, correlationId));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CancelAppointmentRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {
        return ResponseEntity.ok(appointmentService.cancel(id, request, correlationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<List<AppointmentResponse>> findByPatient(@PathVariable @Positive Long patientId) {
        return ResponseEntity.ok(appointmentService.findByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
    public ResponseEntity<List<AppointmentResponse>> findByDoctor(@PathVariable @Positive Long doctorId) {
        return ResponseEntity.ok(appointmentService.findByDoctor(doctorId));
    }
}