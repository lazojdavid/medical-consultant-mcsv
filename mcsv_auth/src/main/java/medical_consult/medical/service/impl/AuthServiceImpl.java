package medical_consult.medical.service.impl;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import medical_consult.medical.dto.LoginRequest;
import medical_consult.medical.dto.LoginResponse;
import medical_consult.medical.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

        public AuthServiceImpl(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
                this.authenticationManager = authenticationManager;
                this.jwtEncoder = jwtEncoder;
        }

    @Value("${auth.jwt.issuer}")
    private String issuer;

    @Value("${auth.jwt.expiration-seconds}")
    private long expirationSeconds;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expirationSeconds))
                .claim("roles", authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .map(authority -> authority.replaceFirst("^ROLE_", ""))
                        .collect(Collectors.toUnmodifiableSet()))
                .build();

                JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
                String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", expirationSeconds);
    }
}