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

    @Query(value = "SELECT * FROM user WHERE deleted = true", nativeQuery = true)
    List<User> findAllDeleted();

    @Query(value = "SELECT * FROM user", nativeQuery = true)
    List<User> findAllWithDeleted();

    Optional<User> findByRut(String rut);

    @Query(value = "SELECT * FROM user WHERE deleted = false", nativeQuery = true)
    List<User> findAllActive();
}
