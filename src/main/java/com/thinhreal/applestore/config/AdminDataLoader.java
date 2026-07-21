package com.thinhreal.applestore.config;

import com.thinhreal.applestore.model.entity.UserEntity;
import com.thinhreal.applestore.model.enums.UserRole;
import com.thinhreal.applestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@groveroot.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@1234}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setFirst_name("Admin");
        admin.setLast_name("User");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setAddress("Grove & Root Orchard");
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);
        log.info("Seeded default admin account: {}", adminEmail);
    }
}
