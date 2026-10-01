package medical_consult.medical.controller;

import jakarta.validation.Valid;
import medical_consult.medical.dto.request.DoctorRequestDto;
import medical_consult.medical.dto.response.DoctorResponseDto;
import medical_consult.medical.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDto> create(@Valid @RequestBody DoctorRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.register(request));
    }
}