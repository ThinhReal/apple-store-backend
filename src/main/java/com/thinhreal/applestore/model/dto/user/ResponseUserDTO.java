package com.thinhreal.applestore.model.dto.user;

import com.thinhreal.applestore.model.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserDTO {
    private Long id;
    private String email;
    private String first_name;
    private String last_name;
    private String address;

    public ResponseUserDTO(UserEntity userEntity){
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.first_name = userEntity.getFirst_name();
        this.last_name = userEntity.getLast_name();
        this.address = userEntity.getAddress();
    }
}

