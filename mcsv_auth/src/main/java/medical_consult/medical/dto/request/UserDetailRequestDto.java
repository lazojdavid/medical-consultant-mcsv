package medical_consult.medical.dto.request;

public record UserDetailRequestDto(
        Long userId,
        String email,
        String name,
        String lastName,
        String address) {
}
