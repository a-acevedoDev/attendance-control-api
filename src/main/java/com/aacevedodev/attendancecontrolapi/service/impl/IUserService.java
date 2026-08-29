package com.aacevedodev.attendancecontrolapi.service.impl;

import com.aacevedodev.attendancecontrolapi.dto.UserCreateDTO;
import com.aacevedodev.attendancecontrolapi.dto.UserUpdateDTO;
import com.aacevedodev.attendancecontrolapi.model.Credential;
import com.aacevedodev.attendancecontrolapi.model.Role;
import com.aacevedodev.attendancecontrolapi.model.User;
import com.aacevedodev.attendancecontrolapi.repository.CredentialRepository;
import com.aacevedodev.attendancecontrolapi.repository.RoleRepository;
import com.aacevedodev.attendancecontrolapi.repository.UserRepository;
import com.aacevedodev.attendancecontrolapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class IUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserCreateDTO createDTO) {

        if (credentialRepository.existsByEmail(createDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado.");
        }

        if (userRepository.existsByRut(createDTO.getRut())) {
            throw new RuntimeException("El RUT ya está registrado.");
        }

        Role role = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Role no encontrado."));

        User user = User.builder()
                .rut(createDTO.getRut())
                .name(createDTO.getName())
                .lastName(createDTO.getLastName())
                .phone(createDTO.getPhone())
                .address(createDTO.getAddress())
                .roles(Set.of(role))
                .build();

        Credential credential = Credential.builder()
                .email(createDTO.getEmail())
                .passwordHash(passwordEncoder.encode(createDTO.getPassword()))
                .enabled(true)
                .user(user)
                .build();

        User userSaved = userRepository.save(user);
        credential.setUser(userSaved);
        Credential credentialSaved = credentialRepository.save(credential);

        return userSaved;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public User updateUser(Integer id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (!user.getRut().equals(updateDTO.getRut()) && userRepository.existsByRut(updateDTO.getRut())) {
            throw new RuntimeException("El RUT " + updateDTO.getRut() + " ya está registrado.");
        }

        user.setRut(updateDTO.getRut());
        user.setName(updateDTO.getName());
        user.setLastName(updateDTO.getLastName());
        user.setPhone(updateDTO.getPhone());
        user.setAddress(updateDTO.getAddress());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        Credential credential = credentialRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Credenciales no encontradas para el usuario ID: " + id));

        credential.setEnabled(false);
        credentialRepository.save(credential);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listUsersByStatus(String estado) {
        if ("activo".equalsIgnoreCase(estado)) {
            return userRepository.findAll();
        } else if ("inactivo".equalsIgnoreCase(estado)) {
            return userRepository.findAllDeleted();
        } else if ("todos".equalsIgnoreCase(estado)) {
            return userRepository.findAllWithDeleted();
        } else {
            throw new RuntimeException("Estado no válido. Use: activo, inactivo o todos");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByRut(String rut) {
        return userRepository.findByRut(rut);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
