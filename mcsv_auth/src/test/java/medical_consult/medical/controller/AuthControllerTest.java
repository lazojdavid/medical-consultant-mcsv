package medical_consult.medical.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import medical_consult.medical.dto.LoginRequest;
import medical_consult.medical.dto.LoginResponse;
import medical_consult.medical.service.AuthService;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginReturnsAccessToken() {
        LoginRequest request = new LoginRequest("admin", "admin");
        LoginResponse response = new LoginResponse("jwt-token", "Bearer", 3600);
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = authController.login(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(authService).login(request);
    }
}