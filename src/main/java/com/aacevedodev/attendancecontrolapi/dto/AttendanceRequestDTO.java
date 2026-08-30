package com.aacevedodev.attendancecontrolapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer userId;

    @NotBlank(message = "El tipo es obligatorio")
    private String type;

    private String ipOrigin;
}
