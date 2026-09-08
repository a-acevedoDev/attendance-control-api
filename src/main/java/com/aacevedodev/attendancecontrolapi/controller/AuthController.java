package com.aacevedodev.attendancecontrolapi.controller;

import com.aacevedodev.attendancecontrolapi.dto.LoginRequestDTO;
import com.aacevedodev.attendancecontrolapi.dto.AuthResponseDTO;
import com.aacevedodev.attendancecontrolapi.dto.UserCreateDTO;
import com.aacevedodev.attendancecontrolapi.model.User;
import com.aacevedodev.attendancecontrolapi.security.JwtService;
import com.aacevedodev.attendancecontrolapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication);

        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fullName = user.getName() + " " + user.getLastName();

        String rol = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName())
                .orElse("EMPLOYEE");

        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .email(authentication.getName())
                .fullName(fullName)
                .rol(rol)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserCreateDTO createDTO) {
        User createdUser = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}