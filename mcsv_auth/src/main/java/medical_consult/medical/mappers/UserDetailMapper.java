package medical_consult.medical.mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import medical_consult.medical.dto.request.UserDetailRequestDto;
import medical_consult.medical.dto.response.UserDetailResponseDto;
import medical_consult.medical.models.UserDetailEntity;
import medical_consult.medical.models.UserEntity;

public final class UserDetailMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private UserDetailMapper() {
    }

    public static UserDetailEntity toEntity(UserDetailRequestDto request) {
        if (request == null) {
            return null;
        }

        return UserDetailEntity.builder()
                .user(toUserReference(request.userId()))
                .email(request.email())
                .name(request.name())
                .lastName(request.lastName())
                .address(request.address())
                .build();
    }

    public static UserDetailResponseDto toDto(UserDetailEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserDetailResponseDto(
                entity.getId(),
                entity.getUser() == null ? null : entity.getUser().getId(),
                entity.getEmail(),
                entity.getName(),
                entity.getLastName(),
                entity.getAddress(),
                format(entity.getCreatedAt()),
                format(entity.getUpdatedAt())
        );
    }

    private static UserEntity toUserReference(Long userId) {
        return userId == null ? null : UserEntity.builder().id(userId).build();
    }

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(FORMATTER);
    }
}