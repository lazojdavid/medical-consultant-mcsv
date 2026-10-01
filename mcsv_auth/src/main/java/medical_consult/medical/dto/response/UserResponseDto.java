package medical_consult.medical.dto.response;

public record UserResponseDto(
        Long id,
        String username,
        String passwordHash,
        String email,
        Boolean active,
        String phone,
        String refreshToken,
        String createdAt,
        String updatedAt) {
}
