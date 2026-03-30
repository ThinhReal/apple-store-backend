package com.thinhreal.applestore.service;

import com.thinhreal.applestore.model.dto.user.RequestUserDTO;
import com.thinhreal.applestore.model.dto.user.ResponseUserDTO;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //Create New User
    public ResponseUserDTO createUser(RequestUserDTO req) {
        UserEntity newUser = new UserEntity(
                req.getFirst_name(), // 1
                req.getLast_name(),  // 2
                req.getEmail(),      // 3
                req.getPassword(),   // 4
                req.getAddress()    //5
        );
        UserEntity savedUser = userRepository.save(newUser);

        return new ResponseUserDTO(savedUser);
    }
    // Get All User
    public List<ResponseUserDTO> getAllUser(){
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(ResponseUserDTO::new).collect(Collectors.toList());
    }

    //Get User By ID
    public ResponseUserDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find the user with id: " + id));
        return new ResponseUserDTO(user);
    }
    //Update User
    public ResponseUserDTO updateUser(Long id, RequestUserDTO req){
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find the user with id: " + id));
        // UserEntity phải có @Setter.
        if (req.getFirst_name() != null && !req.getFirst_name().trim().isEmpty()) {
            existingUser.setFirst_name(req.getFirst_name());
        }

        if (req.getLast_name() != null && !req.getLast_name().trim().isEmpty()) {
            existingUser.setLast_name(req.getLast_name());
        }

        if (req.getAddress() != null && !req.getAddress().trim().isEmpty()) {
            existingUser.setAddress(req.getAddress());
        }

        if (req.getEmail() != null && !req.getEmail().trim().isEmpty()) {
            // (Thực tế nên gọi repository.existsByEmail() ở đây để check trùng)
            existingUser.setEmail(req.getEmail());
        }
        // Vì existingUser ĐÃ CÓ ID, Spring Data JPA sẽ tự động hiểu đây là lệnh UPDATE (chạy hàm merge)
        UserEntity updatedUser = userRepository.save(existingUser);

        return new ResponseUserDTO(updatedUser);
    }
    //Delete User
    public String deleteUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find user with ID: " + id));
        userRepository.delete(user);
        return "Delete Successfully User";
    }
}
