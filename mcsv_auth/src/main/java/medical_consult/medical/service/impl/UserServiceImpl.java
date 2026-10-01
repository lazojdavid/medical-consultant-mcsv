package medical_consult.medical.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import medical_consult.medical.dto.request.UserRequestDto;
import medical_consult.medical.dto.response.UserResponseDto;
import medical_consult.medical.mappers.UserMapper;
import medical_consult.medical.models.UserEntity;
import medical_consult.medical.repository.UserRepository;
import medical_consult.medical.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto save(UserRequestDto request) {
        return UserMapper.toDto(userRepository.save(UserMapper.toEntity(request)));
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto request) {
        UserEntity user = findEntity(id);
        UserEntity updatedUser = UserMapper.toEntity(request);
        updatedUser.setId(user.getId());
        updatedUser.setCreatedAt(user.getCreatedAt());
        return UserMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public UserResponseDto findById(Long id) {
        return UserMapper.toDto(findEntity(id));
    }

    @Override
    public UserResponseDto delete(Long id) {
        UserEntity user = findEntity(id);
        userRepository.delete(user);
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).toList();
    }

    private UserEntity findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}