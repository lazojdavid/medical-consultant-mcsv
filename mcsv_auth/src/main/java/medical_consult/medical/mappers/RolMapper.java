package medical_consult.medical.mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import medical_consult.medical.dto.request.RolRequestDto;
import medical_consult.medical.dto.response.RolResponseDto;
import medical_consult.medical.models.RolEntity;

public final class RolMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private RolMapper() {
    }

    public static RolEntity toEntity(RolRequestDto request) {
        if (request == null) {
            return null;
        }

        return RolEntity.builder()
                .description(request.description())
                .build();
    }

    public static RolResponseDto toDto(RolEntity entity) {
        if (entity == null) {
            return null;
        }

        return new RolResponseDto(
                entity.getId(),
                entity.getDescription(),
                format(entity.getCreatedAt()),
                format(entity.getUpdatedAt())
        );
    }
    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(FORMATTER);
    }
}