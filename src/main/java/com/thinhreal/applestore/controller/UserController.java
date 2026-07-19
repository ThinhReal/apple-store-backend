package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.api.UsersApi;
import com.thinhreal.applestore.api.model.RequestUserDTO;
import com.thinhreal.applestore.api.model.ResponseUserDTO;
import com.thinhreal.applestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<ResponseUserDTO> createUser(RequestUserDTO requestUserDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(requestUserDTO));
    }

    @Override
    public ResponseEntity<List<ResponseUserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<ResponseUserDTO> getUserById(Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<ResponseUserDTO> updateUser(Long id, RequestUserDTO requestUserDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestUserDTO));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
