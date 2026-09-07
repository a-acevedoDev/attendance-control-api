package com.aacevedodev.attendancecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {

    private Long presentToday;
    private Long lateToday;
    private Long absentToday;
}