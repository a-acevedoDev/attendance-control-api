package com.aacevedodev.attendancecontrolapi.service.impl;

import com.aacevedodev.attendancecontrolapi.dto.*;
import com.aacevedodev.attendancecontrolapi.model.AttendanceRecord;
import com.aacevedodev.attendancecontrolapi.model.User;
import com.aacevedodev.attendancecontrolapi.model.enums.AttendanceType;
import com.aacevedodev.attendancecontrolapi.repository.AttendanceRecordRepository;
import com.aacevedodev.attendancecontrolapi.repository.UserRepository;
import com.aacevedodev.attendancecontrolapi.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IAttendanceService implements AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.attendance.entry}")
    private String attendanceEntry;

    @Value("${app.attendance.departure}")
    private String attendanceDeparture;

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
    public List<LateArrivalReportDTO> getLateArrivalsReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        Timestamp start = Timestamp.valueOf(date.atStartOfDay());
        Timestamp end = Timestamp.valueOf(date.atTime(23, 59, 59));

        List<AttendanceRecord> attendance = attendanceRepository.findLateArrivals(start, end, attendanceEntry);

        return attendance.stream()
                .map(r -> new LateArrivalReportDTO(
                        r.getUser().getId(),
                        r.getUser().getRut(),
                        r.getUser().getName() + " " + r.getUser().getLastName(),
                        r.getDate().toLocalDateTime(),
                        delayMinute(r.getDate())
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EarlyDepartureReportDTO> getEarlyDeparturesReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        Timestamp start = Timestamp.valueOf(date.atStartOfDay());
        Timestamp end = Timestamp.valueOf(date.atTime(23, 59, 59));

        List<AttendanceRecord> attendance = attendanceRepository.findEarlyDepartures(start, end, attendanceDeparture);

        return attendance.stream()
                .map(r -> new EarlyDepartureReportDTO(
                        r.getUser().getId(),
                        r.getUser().getRut(),
                        r.getUser().getName() + " " + r.getUser().getLastName(),
                        r.getDate().toLocalDateTime(),
                        earlyMinute(r.getDate())
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbsenteeismReportDTO> getAbsenteeismReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<User> user = attendanceRepository.findAbsentUsers(date.toString());
        LocalDate finalDate = date;
        return user.stream()
                .map(u -> new AbsenteeismReportDTO(
                        u.getId(),
                        u.getRut(),
                        u.getName() + " " + u.getLastName(),
                        finalDate
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceByUser(Integer userId) {
        return attendanceRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getAttendanceByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp end = Timestamp.valueOf(endDate.atTime(23, 59, 59));

        return attendanceRepository.findByUserIdAndDateBetween(userId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCheckedInToday(Integer userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        return attendanceRepository.existsByUserIdAndTypeAttendanceAndDateBetween(
                userId,
                AttendanceType.ENTRADA,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end)
        );
    }

    private int delayMinute(Timestamp entry) {
        LocalDateTime entryLdt = entry.toLocalDateTime();
        LocalDateTime limit = entryLdt.toLocalDate().atTime(LocalTime.parse(attendanceEntry));

        if (entryLdt.isAfter(limit)) {
            return (int) java.time.Duration.between(limit, entryLdt).toMinutes();
        }
        return 0;
    }

    private int earlyMinute(Timestamp entry) {
        LocalDateTime entryLdt = entry.toLocalDateTime();
        LocalDateTime limit = entryLdt.toLocalDate().atTime(LocalTime.parse(attendanceDeparture));

        if (entryLdt.isBefore(limit)) {
            return (int) java.time.Duration.between(entryLdt, limit).toMinutes();
        }
        return 0;
    }
}
