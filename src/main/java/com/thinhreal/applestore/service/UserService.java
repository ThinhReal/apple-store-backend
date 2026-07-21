package com.thinhreal.applestore.service;

import com.thinhreal.applestore.api.model.RequestUserDTO;
import com.thinhreal.applestore.api.model.ResponseUserDTO;
import com.thinhreal.applestore.exception.BusinessException;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.model.enums.UserRole;
import com.thinhreal.applestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseUserDTO createUser(RequestUserDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("An account with this email already exists");
        }

        UserEntity newUser = new UserEntity(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getAddress()
        );
        newUser.setRole(UserRole.CUSTOMER);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return toDto(userRepository.save(newUser));
    }

    public List<ResponseUserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ResponseUserDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find the user with id: " + id));
        return toDto(user);
    }

    public ResponseUserDTO updateUser(Long id, RequestUserDTO request) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find the user with id: " + id));
        applyDto(existingUser, request);
        UserEntity updatedUser = userRepository.save(existingUser);
        return toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cannot find user with ID: " + id));
        userRepository.delete(user);
    }

    private ResponseUserDTO toDto(UserEntity entity) {
        ResponseUserDTO dto = new ResponseUserDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirst_name());
        dto.setLastName(entity.getLast_name());
        dto.setAddress(entity.getAddress());
        return dto;
    }
    private void applyDto(UserEntity entity, RequestUserDTO dto) {
        entity.setFirst_name(dto.getFirstName());
        entity.setLast_name(dto.getLastName());
        entity.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        entity.setAddress(dto.getAddress());
    }
}
