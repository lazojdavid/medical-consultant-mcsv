package medical_consult.medical.service.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import medical_consult.medical.models.UserEntity;
import medical_consult.medical.repository.UserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final InMemoryUserDetailsManager demoUsers;

    public DatabaseUserDetailsService(
            UserRepository userRepository,
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.demoUsers = new InMemoryUserDetailsManager(
                User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("ADMINISTRADOR").build(),
                User.withUsername("medico").password(passwordEncoder.encode("medico")).roles("MEDICO").build(),
                User.withUsername("recepcionista").password(passwordEncoder.encode("recepcionista"))
                        .roles("RECEPCIONISTA").build(),
                User.withUsername("paciente").password(passwordEncoder.encode("paciente")).roles("PACIENTE").build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(this::toUserDetails)
                .orElseGet(() -> demoUsers.loadUserByUsername(username));
    }

    private UserDetails toUserDetails(UserEntity user) {
        List<SimpleGrantedAuthority> authorities = jdbcTemplate.query(
                "SELECT r.description FROM rols r "
                        + "JOIN rols_user ur ON ur.rol_id = r.rol_id WHERE ur.user_id = ?",
                (resultSet, rowNum) -> new SimpleGrantedAuthority(
                        "ROLE_" + resultSet.getString("description").replaceFirst("^ROLE_", "")),
                user.getId());

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!Boolean.TRUE.equals(user.getActive()))
                .authorities(authorities)
                .build();
    }
}