package com.aacevedodev.attendancecontrolapi.controller;

import com.aacevedodev.attendancecontrolapi.dto.AuthResponseDTO;
import com.aacevedodev.attendancecontrolapi.dto.LoginRequestDTO;
import com.aacevedodev.attendancecontrolapi.model.Credential;
import com.aacevedodev.attendancecontrolapi.repository.CredentialRepository;
import com.aacevedodev.attendancecontrolapi.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CredentialRepository credentialRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        Credential credential = credentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales no encontradas"));

        String nombreCompleto = credential.getUser().getName() + " " + credential.getUser().getLastName();
        String rol = credential.getUser().getRoles().stream()
                .findFirst()
                .map(role -> role.getName().replace("ROLE_", ""))
                .orElse("USER");

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .token(token)
                .email(request.getEmail())
                .nombreCompleto(nombreCompleto)
                .rol(rol)
                .build());
    }
}