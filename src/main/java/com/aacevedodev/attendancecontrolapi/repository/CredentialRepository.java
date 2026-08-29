package com.aacevedodev.attendancecontrolapi.repository;

import com.aacevedodev.attendancecontrolapi.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Integer> {
    boolean existsByEmail(String email);
    Optional<Credential> findByUserId(Integer id);
}
