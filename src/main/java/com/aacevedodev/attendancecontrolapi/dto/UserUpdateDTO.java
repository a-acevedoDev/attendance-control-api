package com.aacevedodev.attendancecontrolapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserUpdateDTO {

    @NotBlank
    private String rut;

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    private String phone;

    private String address;
}
