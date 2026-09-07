package com.aacevedodev.attendancecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRecordDTO {

    private Integer userId;
    private String fullName;
    private LocalTime entryTime;
    private LocalTime departureTime;
    private String status;

}