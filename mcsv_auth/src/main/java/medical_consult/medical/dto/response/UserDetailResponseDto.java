package medical_consult.medical.dto.response;

public record UserDetailResponseDto(
        Long id,
        Long userId,
        String email,
        String name,
        String lastName,
        String address,
        String createdAt,
        String updatedAt) {
}
