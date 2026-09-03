package com.aacevedodev.attendancecontrolapi.config;

import com.aacevedodev.attendancecontrolapi.model.Role;
import com.aacevedodev.attendancecontrolapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        String[] roles = {"ADMIN", "EMPLOYEE"};
        for (String roleName : roles) {
            if (!roleRepository.findByName(roleName).isPresent()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}