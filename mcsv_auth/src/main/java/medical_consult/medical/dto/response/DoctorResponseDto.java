package medical_consult.medical.dto.response;

public record DoctorResponseDto(
        Long userId,
        String username,
        String email,
        String name,
        String lastName,
        String role) {
}