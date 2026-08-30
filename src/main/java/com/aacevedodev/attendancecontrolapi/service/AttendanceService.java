package com.aacevedodev.attendancecontrolapi.service;

import com.aacevedodev.attendancecontrolapi.dto.*;
import com.aacevedodev.attendancecontrolapi.model.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceRecord checkIn(Integer userId, String ipOrigen);
    AttendanceRecord checkOut(Integer userId, String ipOrigen);
    AttendanceResponseDTO registerAttendance(AttendanceRequestDTO requestDTO);
    List<LateArrivalReportDTO> getLateArrivalsReport(LocalDate fecha);
    List<EarlyDepartureReportDTO> getEarlyDeparturesReport(LocalDate fecha);
    List<AbsenteeismReportDTO> getAbsenteeismReport(LocalDate fecha);
    List<AttendanceRecord> getAttendanceByUser(Integer userId);
    List<AttendanceRecord> getAttendanceByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate);
    boolean hasCheckedInToday(Integer userId);
}
