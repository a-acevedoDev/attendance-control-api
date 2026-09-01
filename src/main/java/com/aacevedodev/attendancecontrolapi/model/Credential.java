package com.aacevedodev.attendancecontrolapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "credential")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false, unique = true)
    @Email(message = "El correo debe tener un formato valido.")
    @Size(max = 100, message = "El email debe tener maximo 100 caracteres.")
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener minimo 8 caracteres.")
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    private Timestamp updateAt;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
}
