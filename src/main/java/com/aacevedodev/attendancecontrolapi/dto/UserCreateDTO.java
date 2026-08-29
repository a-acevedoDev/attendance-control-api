package com.aacevedodev.attendancecontrolapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCreateDTO {

    @NotBlank
    private String rut;

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    private String phone;

    private String address;

    @NotBlank
    @Email(message = "Formato de email incorrecto.")
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;
}
