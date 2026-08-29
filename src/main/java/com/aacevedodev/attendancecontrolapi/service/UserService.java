package com.aacevedodev.attendancecontrolapi.service;

import com.aacevedodev.attendancecontrolapi.dto.UserCreateDTO;
import com.aacevedodev.attendancecontrolapi.dto.UserUpdateDTO;
import com.aacevedodev.attendancecontrolapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserCreateDTO createDTO);
    User getUserById(Integer id);
    User updateUser(Integer id, UserUpdateDTO updateDTO);
    void deleteUser(Integer id);
    List<User> listUsersByStatus(String estado);
    Page<User> listUsers(Pageable pageable);
    List<User> getActiveUsers();
    Optional<User> findByRut(String rut);
    Optional<User> findByEmail(String email);
}
