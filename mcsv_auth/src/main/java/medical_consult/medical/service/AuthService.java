package medical_consult.medical.service;

import medical_consult.medical.dto.LoginRequest;
import medical_consult.medical.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}