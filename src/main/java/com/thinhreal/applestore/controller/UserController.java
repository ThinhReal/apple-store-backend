package com.thinhreal.applestore.controller;

import com.thinhreal.applestore.model.dto.user.RequestUserDTO;
import com.thinhreal.applestore.model.dto.user.ResponseUserDTO;
import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseUserDTO createUser(@RequestBody RequestUserDTO req) {
        return userService.createUser(req);
    }
    @GetMapping
    public List<ResponseUserDTO> getAllUser() {
        return userService.getAllUser();
    }
    @GetMapping("/{id}")
    public ResponseUserDTO getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }
    @PatchMapping("/{id}")
    public ResponseUserDTO updateUser(@PathVariable Long id,@RequestBody RequestUserDTO req){
        return userService.updateUser(id, req);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }


}
