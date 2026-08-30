package com.aacevedodev.attendancecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarlyDepartureReportDTO {
    private Integer userId;
    private String rut;
    private String fullName;
    private LocalDateTime departure;
    private Integer delay;
}
