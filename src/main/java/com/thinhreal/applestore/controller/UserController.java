package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    @PostMapping
    public UserEntity createUser(@RequestBody UserEntity user){
        return userRepository.save(user);
    }
}
