package medical_consult.medical.dto.request;

public record UserRequestDto(
        String username,
        String passwordHash,
        String email,
        Boolean active,
        String phone,
        String refreshToken) {
}
