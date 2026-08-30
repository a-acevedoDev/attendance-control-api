package com.aacevedodev.attendancecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponseDTO {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private String type;
    private LocalDateTime date;
    private String ipOrigin;
}
