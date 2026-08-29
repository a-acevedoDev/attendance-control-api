package com.aacevedodev.attendancecontrolapi.repository;

import com.aacevedodev.attendancecontrolapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByRut(String rut);

    @Query("SELECT u FROM User u")
    List<User> findAllWithDeleted();

    @Query("SELECT u FROM User u WHERE u.deleted = true")
    List<User> findAllDeleted();

    Optional<User> findByRut(String rut);

    Optional<User> findByEmail(String email);
}
