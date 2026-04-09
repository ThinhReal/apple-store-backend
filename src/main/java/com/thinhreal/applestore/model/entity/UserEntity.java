package com.thinhreal.applestore.model.entity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import org.apache.commons.validator.Validator;
import lombok.Setter;
import org.apache.commons.validator.routines.EmailValidator;
//import class from passy to validate password
import org.hibernate.annotations.UpdateTimestamp;
import org.passay.PasswordValidator;
import org.passay.CharacterRule;
import org.passay.LengthRule;
import org.passay.EnglishCharacterData;
import org.passay.WhitespaceRule;
import org.passay.RuleResult;
import org.passay.PasswordData;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", unique = true)
    private String email;

    //Parent of Order
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<OrderEntity> orders;

    private String password;
    private String first_name;
    private String last_name;
    private String address;

    // Constructor
    public UserEntity (String first_name, String last_name, String email, String password, String address) {
        // FIRST$LAST NAME VALIDATION
        if (first_name == null || first_name.trim().isEmpty() || last_name == null || last_name.trim().isEmpty()) {
            throw new IllegalArgumentException("Your First or Last Name is empty, please check again");
        }
        this.first_name = first_name;
        this.last_name = last_name;
        
        // EMAIL VALIDATION
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing Email Field");
        }
        EmailValidator validator = EmailValidator.getInstance();
        if (validator.isValid(email)) {
            this.email = email;
        }else{
            throw new IllegalArgumentException("Invalid Email");
        }
    
        // PASSWORD VALIDATION
        //1. Define password rules
        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
                // Length rule: Must be between 8 and 30 characters
                new LengthRule(8, 30),
                // Must contain at least 1 uppercase letter
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // Must contain at least 1 lowercase letter
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // Must contain at least 1 number (digit)
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // Must contain at least 1 special character (e.g., !, @, #, $, etc.)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // Must not contain spaces or tabs
                new WhitespaceRule()
        ));

        // 2. Package the user's input into a PasswordData object
        PasswordData userPassword = new PasswordData(password);

        // 3. Run the validation
        RuleResult result = passwordValidator.validate(userPassword);

        // 4. Return true if it passes all rules, false otherwise
        if (result.isValid()){
            this.password = password;
        }
        else{
            throw new IllegalArgumentException("Password does not meet the criteria");
        }
    
        // ADDRESS VALIDATION
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Your address is empty, please check again");
        }
        this.address = address;
}
}

