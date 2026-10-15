package com.aacevedodev.attendancecontrolapi.controller;

import com.aacevedodev.attendancecontrolapi.dto.*;
import com.aacevedodev.attendancecontrolapi.model.AttendanceRecord;
import com.aacevedodev.attendancecontrolapi.model.Credential;
import com.aacevedodev.attendancecontrolapi.repository.CredentialRepository;
import com.aacevedodev.attendancecontrolapi.service.AttendanceService;
import com.aacevedodev.attendancecontrolapi.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final CredentialRepository credentialRepository;
    private final IpUtil ipUtil;

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<AttendanceRecord> checkIn(Authentication authentication, HttpServletRequest request) {
        String email = authentication.getName();
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales no encontradas"));
        Integer userId = credential.getUser().getId();
        String ipOrigen = ipUtil.getClientIp(request);
        AttendanceRecord record = attendanceService.checkIn(userId, ipOrigen);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<AttendanceRecord> checkOut(Authentication authentication, HttpServletRequest request) {
        String email = authentication.getName();
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales no encontradas"));
        Integer userId = credential.getUser().getId();
        String ipOrigen = ipUtil.getClientIp(request);
        AttendanceRecord record = attendanceService.checkOut(userId, ipOrigen);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<AttendanceResponseDTO> registerAttendance(
            @Valid @RequestBody AttendanceRequestDTO requestDTO,
            HttpServletRequest request) {
        String ipOrigen = ipUtil.getClientIp(request);
        requestDTO.setIpOrigin(ipOrigen);
        AttendanceResponseDTO response = attendanceService.registerAttendance(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/late-arrivals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LateArrivalReportDTO>> getLateArrivalsReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getLateArrivalsReport(date));
    }

    @GetMapping("/reports/early-departures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EarlyDepartureReportDTO>> getEarlyDeparturesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getEarlyDeparturesReport(date));
    }

    @GetMapping("/reports/absenteeism")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AbsenteeismReportDTO>> getAbsenteeismReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAbsenteeismReport(date));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        return ResponseEntity.ok(attendanceService.getDashboardMetrics());
    }

    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DailyRecordDTO>> getTodayRecords() {
        return ResponseEntity.ok(attendanceService.getTodayRecords());
    }

    @GetMapping("/my-weekly-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<WeeklyHistoryDTO>> getMyWeeklyHistory(Authentication authentication) {
        String email = authentication.getName();
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales no encontradas"));
        Integer userId = credential.getUser().getId();
        return ResponseEntity.ok(attendanceService.getMyWeeklyHistory(userId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByUser(userId));
    }

    @GetMapping("/user/{userId}/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceByUserAndDateRange(
            @PathVariable Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceByUserAndDateRange(userId, startDate, endDate));
    }

    @GetMapping("/has-checked-in-today")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Boolean> hasCheckedInToday(Authentication authentication) {
        String email = authentication.getName();
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales no encontradas"));
        Integer userId = credential.getUser().getId();
        return ResponseEntity.ok(attendanceService.hasCheckedInToday(userId));
    }
}