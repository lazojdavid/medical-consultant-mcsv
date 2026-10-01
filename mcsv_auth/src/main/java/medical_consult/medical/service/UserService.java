package medical_consult.medical.service;

import java.util.List;

import medical_consult.medical.commons.ICrudCommons;
import medical_consult.medical.dto.request.UserRequestDto;
import medical_consult.medical.dto.response.UserResponseDto;

public interface UserService extends ICrudCommons<UserRequestDto, UserResponseDto, Long> {

    List<UserResponseDto> getAll();
}