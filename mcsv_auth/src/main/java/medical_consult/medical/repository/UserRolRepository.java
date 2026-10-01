package medical_consult.medical.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import medical_consult.medical.models.UserRolEntity;

public interface UserRolRepository extends JpaRepository<UserRolEntity, Long> {

    List<UserRolEntity> findByUserId(Long userId);
}