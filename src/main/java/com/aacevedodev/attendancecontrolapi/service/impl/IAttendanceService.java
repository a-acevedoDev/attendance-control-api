package com.aacevedodev.attendancecontrolapi.service.impl;

import com.aacevedodev.attendancecontrolapi.dto.*;
import com.aacevedodev.attendancecontrolapi.model.AttendanceRecord;
import com.aacevedodev.attendancecontrolapi.model.User;
import com.aacevedodev.attendancecontrolapi.model.enums.AttendanceType;
import com.aacevedodev.attendancecontrolapi.repository.AttendanceRecordRepository;
import com.aacevedodev.attendancecontrolapi.repository.UserRepository;
import com.aacevedodev.attendancecontrolapi.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IAttendanceService implements AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AttendanceRecord checkIn(Integer userId, String ipOrigen) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario con ID: " + userId));

        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                .typeAttendance(AttendanceType.ENTRADA)
                .ipOrigin(ipOrigen)
                .user(user)
                .build();

        return attendanceRepository.save(attendanceRecord);
    }

    @Override
    @Transactional
    public AttendanceRecord checkOut(Integer userId, String ipOrigen) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario con ID: " + userId));

        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                .typeAttendance(AttendanceType.SALIDA)
                .ipOrigin(ipOrigen)
                .user(user)
                .build();

        return attendanceRepository.save(attendanceRecord);
    }

    @Override
    @Transactional
    public AttendanceResponseDTO registerAttendance(AttendanceRequestDTO requestDTO) {
        AttendanceType type = switch (requestDTO.getType().toUpperCase()) {
            case "ENTRADA" -> AttendanceType.ENTRADA;
            case "SALIDA" -> AttendanceType.SALIDA;
            default -> throw new RuntimeException("Tipo inválido: " + requestDTO.getType() +
                    ". Debe ser 'ENTRADA' o 'SALIDA'");
        };

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("El usuario ID: " + requestDTO.getUserId() + " no fue encontrado."));

        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                .typeAttendance(type)
                .date(Timestamp.from(Instant.now()))
                .ipOrigin(requestDTO.getIpOrigin())
                .user(user)
                .build();

        AttendanceRecord attendanceSaved = attendanceRepository.save(attendanceRecord);

        AttendanceResponseDTO responseDTO = AttendanceResponseDTO.builder()
                .id(attendanceSaved.getId())
                .userId(attendanceSaved.getUser().getId())
                .userFullName(attendanceSaved.getUser().getName() + " " + attendanceSaved.getUser().getLastName())
                .type(attendanceSaved.getTypeAttendance().name())
                .date(attendanceSaved.getDate().toLocalDateTime())
                .ipOrigin(attendanceSaved.getIpOrigin())
                .build();

        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LateArrivalReportDTO> getLateArrivalsReport(LocalDate fecha) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EarlyDepartureReportDTO> getEarlyDeparturesReport(LocalDate fecha) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbsenteeismReportDTO> getAbsenteeismReport(LocalDate fecha) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceByUser(Integer userId) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCheckedInToday(Integer userId) {
        return false;
    }
}
