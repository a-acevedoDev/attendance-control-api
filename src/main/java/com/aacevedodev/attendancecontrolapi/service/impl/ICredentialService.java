package com.aacevedodev.attendancecontrolapi.service.impl;

import com.aacevedodev.attendancecontrolapi.dto.ChangePasswordDTO;
import com.aacevedodev.attendancecontrolapi.model.Credential;
import com.aacevedodev.attendancecontrolapi.repository.CredentialRepository;
import com.aacevedodev.attendancecontrolapi.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class ICredentialService implements CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<Credential> findByEmail(String email) {
        return credentialRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, ChangePasswordDTO changePasswordDTO) {
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas nuevas no coinciden");
        }

        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Las credenciales del usuario ID: " + userId + " no se encontraron."));

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), credential.getPasswordHash())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        credential.setPasswordHash(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        credential.setUpdateAt(Timestamp.from(Instant.now()));
        credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public void lockCredentials(Integer userId) {
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Las credenciales del usuario ID: " + userId + " no se encontraron."));
        credential.setEnabled(false);
        credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public void unlockCredentials(Integer userId) {
        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Las credenciales del usuario ID: " + userId + " no se encontraron."));
        credential.setEnabled(true);
        credentialRepository.save(credential);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return credentialRepository.existsByEmail(email);
    }
}
