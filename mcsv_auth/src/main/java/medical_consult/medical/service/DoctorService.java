package medical_consult.medical.service;

import medical_consult.medical.dto.request.DoctorRequestDto;
import medical_consult.medical.dto.response.DoctorResponseDto;

public interface DoctorService {

    DoctorResponseDto register(DoctorRequestDto request);
}