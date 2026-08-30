package com.aacevedodev.attendancecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenteeismReportDTO {
    private Integer userId;
    private String rut;
    private String fullName;
    private LocalDate date;
}
