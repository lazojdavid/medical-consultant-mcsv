package medical_consult.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import medical_consult.medical.models.RolEntity;

public interface RolRepository extends JpaRepository<RolEntity, Long> {
}