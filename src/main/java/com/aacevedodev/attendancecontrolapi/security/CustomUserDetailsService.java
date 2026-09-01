package com.aacevedodev.attendancecontrolapi.security;

import com.aacevedodev.attendancecontrolapi.model.Credential;
import com.aacevedodev.attendancecontrolapi.model.Role;
import com.aacevedodev.attendancecontrolapi.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        Set<SimpleGrantedAuthority> authorities = credential.getUser().getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        return User.builder()
                .username(credential.getEmail())
                .password(credential.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .credentialsExpired(false)
                .disabled(!credential.isEnabled())
                .build();
    }
}