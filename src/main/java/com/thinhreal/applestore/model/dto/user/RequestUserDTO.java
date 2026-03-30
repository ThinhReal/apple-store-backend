package com.thinhreal.applestore.model.dto.user;

import com.thinhreal.applestore.model.entity.OrderEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserDTO {
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private String address;
}
