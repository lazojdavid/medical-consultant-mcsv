package medical_consult.medical.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn) {
}