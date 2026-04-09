package com.thinhreal.applestore.service;

import com.thinhreal.applestore.config.ModelMapperConfig;
import com.thinhreal.applestore.model.dto.user.RequestUserDTO;
import com.thinhreal.applestore.model.dto.user.ResponseUserDTO;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    // dùng modelMapper class gốc của thư viện
    private final ModelMapper modelMapper;
    //Create New User
    public ResponseUserDTO createUser(RequestUserDTO req) {
        UserEntity newUser = new UserEntity(
                req.getFirst_name(), // 1
                req.getLast_name(),  // 2
                req.getEmail(),      // 3
                req.getPassword(),   // 4
                req.getAddress()    //5
        );
        // Now the data is in Transient, only in RAM , we need to save it
        UserEntity savedUser = userRepository.save(newUser);

        return modelMapper.map(savedUser, ResponseUserDTO.class);
    }
    // Get All User
    public List<ResponseUserDTO> getAllUser(){
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, ResponseUserDTO.class))
                .collect(Collectors.toList());
    }

    //Get User By ID
    public ResponseUserDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find the user with id: " + id));
        return modelMapper.map(user, ResponseUserDTO.class);
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
            //Should add 1 function to check the email duplication here before set directly to entity.

            existingUser.setEmail(req.getEmail());
        }
        // existingUser already had ID, so Spring Data JPA automatically use .save as .merge (update)
        UserEntity updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, ResponseUserDTO.class) ;
    }
    //Delete User
    public String deleteUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cannot find user with ID: " + id));
        userRepository.delete(user);
        return "Delete Successfully User";
    }
}
