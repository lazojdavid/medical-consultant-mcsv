package medical_consult.medical.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import medical_consult.medical.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
}