package com.thinhreal.applestore.model.dto.user;

import com.thinhreal.applestore.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseUserDTO {
    private Long id;
    private String email;
    private String first_name;
    private String last_name;
    private String address;
}

