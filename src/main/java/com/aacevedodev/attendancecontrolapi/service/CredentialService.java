package com.aacevedodev.attendancecontrolapi.service;

import com.aacevedodev.attendancecontrolapi.dto.ChangePasswordDTO;
import com.aacevedodev.attendancecontrolapi.model.Credential;

import java.util.Optional;

public interface CredentialService {
    Optional<Credential> findByEmail(String email);
    void changePassword(Integer userId, ChangePasswordDTO changePasswordDTO);
    void lockCredentials(Integer userId);
    void unlockCredentials(Integer userId);
    boolean existsByEmail(String email);
}
