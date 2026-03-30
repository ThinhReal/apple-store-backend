package com.thinhreal.applestore.model.dto.user;

import com.thinhreal.applestore.model.entity.OrderEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserDTO {
    private String email;
    private String first_name;
    private String last_name;
    private String address;
}
