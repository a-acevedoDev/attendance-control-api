package com.aacevedodev.attendancecontrolapi.controller;

import com.aacevedodev.attendancecontrolapi.dto.UserCreateDTO;
import com.aacevedodev.attendancecontrolapi.dto.UserManagementDTO;
import com.aacevedodev.attendancecontrolapi.dto.UserUpdateDTO;
import com.aacevedodev.attendancecontrolapi.model.User;
import com.aacevedodev.attendancecontrolapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        User createdUser = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false, defaultValue = "activo") String estado) {
        List<User> users = userService.listUsersByStatus(estado);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsersPaginated(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<User> users = userService.listUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/management")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserManagementDTO>> getAllUsersForManagement() {
        List<UserManagementDTO> users = userService.getAllUsersForManagement();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        User updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}