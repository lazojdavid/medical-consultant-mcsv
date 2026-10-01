package medical_consult.medical.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import medical_consult.medical.dto.request.DoctorRequestDto;
import medical_consult.medical.dto.response.DoctorResponseDto;
import medical_consult.medical.service.DoctorService;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private static final String DOCTOR_ROLE = "MEDICO";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public DoctorResponseDto register(DoctorRequestDto request) {
        List<Long> roleIds = jdbcTemplate.query(
                "SELECT rol_id FROM rols WHERE description = ?",
                (resultSet, rowNum) -> resultSet.getLong("rol_id"),
                DOCTOR_ROLE);
        if (roleIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rol MEDICO no está configurado");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        Long userId = jdbcTemplate.queryForObject(
                "INSERT INTO users (username, passwordhash, email, is_active, phone) "
                        + "VALUES (?, ?, ?, TRUE, ?) RETURNING user_id",
                Long.class,
                request.username(), passwordHash, request.email(), request.phone());

        jdbcTemplate.update(
                "INSERT INTO users_details (user_id, email, name, last_name, address) VALUES (?, ?, ?, ?, ?)",
                userId, request.email(), request.name(), request.lastName(), request.address());
        jdbcTemplate.update(
                "INSERT INTO rols_user (rol_id, user_id) VALUES (?, ?)",
                roleIds.get(0), userId);

        return new DoctorResponseDto(
                userId, request.username(), request.email(), request.name(), request.lastName(), DOCTOR_ROLE);
    }
}