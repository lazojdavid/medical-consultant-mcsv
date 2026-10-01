package medical_consult.medical.mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import medical_consult.medical.dto.request.UserRolRequestDto;
import medical_consult.medical.dto.response.UserRolResponseDto;
import medical_consult.medical.models.RolEntity;
import medical_consult.medical.models.UserEntity;
import medical_consult.medical.models.UserRolEntity;

public final class UserRolMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private UserRolMapper() {
    }

    public static UserRolEntity toEntity(UserRolRequestDto request) {
        if (request == null) {
            return null;
        }

        return UserRolEntity.builder()
                .role(toRoleReference(request.roleId()))
                .user(toUserReference(request.userId()))
                .build();
    }

    public static UserRolResponseDto toDto(UserRolEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserRolResponseDto(
                entity.getId(),
                entity.getRole() == null ? null : entity.getRole().getId(),
                entity.getUser() == null ? null : entity.getUser().getId(),
                format(entity.getCreatedAt()),
                format(entity.getUpdatedAt())
        );
    }

    private static RolEntity toRoleReference(Long roleId) {
        return roleId == null ? null : RolEntity.builder().id(roleId).build();
    }

    private static UserEntity toUserReference(Long userId) {
        return userId == null ? null : UserEntity.builder().id(userId).build();
    }

    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(FORMATTER);
    }
}