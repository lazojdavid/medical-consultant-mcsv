package medical_consult.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import medical_consult.medical.models.UserDetailEntity;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {
}