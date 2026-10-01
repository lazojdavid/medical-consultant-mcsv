package medical_consult.medical.mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import medical_consult.medical.dto.request.UserRequestDto;
import medical_consult.medical.dto.response.UserResponseDto;
import medical_consult.medical.models.UserEntity;

public final class UserMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private UserMapper() {
    }

    public static UserEntity toEntity(UserRequestDto request) {
        if (request == null) {
            return null;
        }

        return UserEntity.builder()
                .username(request.username())
                .passwordHash(request.passwordHash())
                .email(request.email())
                .active(request.active())
                .phone(request.phone())
                .refreshToken(request.refreshToken())
                .build();
    }

    public static UserResponseDto toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserResponseDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getEmail(),
                entity.getActive(),
                entity.getPhone(),
                entity.getRefreshToken(),
                format(entity.getCreatedAt()),
                format(entity.getUpdatedAt())
        );
    }

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(FORMATTER);
    }
}