package medical_consult.medical.dto.response;

public record UserRolResponseDto(
        Long id,
        Long roleId,
        Long userId,
        String createdAt,
        String updatedAt) {
}
