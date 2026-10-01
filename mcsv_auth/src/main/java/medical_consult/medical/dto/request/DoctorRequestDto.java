package medical_consult.medical.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DoctorRequestDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank @Email String email,
        String phone,
        @NotBlank String name,
        @NotBlank String lastName,
        String address) {
}