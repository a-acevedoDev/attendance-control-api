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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDTO getDashboardMetrics() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long totalActiveUsers = userRepository.count();

        long presentToday = attendanceRepository.countDistinctByUserIdAndTypeAndDateBetween(
                AttendanceType.ENTRADA,
                Timestamp.valueOf(startOfDay),
                Timestamp.valueOf(endOfDay)
        );

        long lateToday = attendanceRepository.countLateArrivalsToday(
                Timestamp.valueOf(startOfDay),
                Timestamp.valueOf(endOfDay),
                attendanceEntry
        );

        long absentToday = totalActiveUsers - presentToday;

        return DashboardMetricsDTO.builder()
                .presentToday(presentToday)
                .lateToday(lateToday)
                .absentToday(absentToday)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyRecordDTO> getTodayRecords() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<User> activeUsers = userRepository.findAll();

        List<AttendanceRecord> todayRecords = attendanceRepository
                .findByDateBetween(Timestamp.valueOf(start), Timestamp.valueOf(end));

        Map<Integer, AttendanceRecord> entryMap = todayRecords.stream()
                .filter(r -> r.getTypeAttendance() == AttendanceType.ENTRADA)
                .collect(Collectors.toMap(
                        r -> r.getUser().getId(),
                        r -> r,
                        (existing, replacement) -> existing
                ));

        Map<Integer, AttendanceRecord> departureMap = todayRecords.stream()
                .filter(r -> r.getTypeAttendance() == AttendanceType.SALIDA)
                .collect(Collectors.toMap(
                        r -> r.getUser().getId(),
                        r -> r,
                        (existing, replacement) -> existing
                ));

        LocalTime lateLimit = LocalTime.parse(attendanceEntry);
        LocalTime earlyDepartureLimit = LocalTime.parse(attendanceDeparture);

        return activeUsers.stream()
                .map(user -> {
                    AttendanceRecord entry = entryMap.get(user.getId());
                    AttendanceRecord departure = departureMap.get(user.getId());

                    LocalTime entryTime = entry != null ? entry.getDate().toLocalDateTime().toLocalTime() : null;
                    LocalTime departureTime = departure != null ? departure.getDate().toLocalDateTime().toLocalTime() : null;

                    String status;

                    if (entryTime == null) {
                        status = "INASISTENTE";
                    } else if (entryTime.isAfter(lateLimit)) {
                        status = "ATRASADO";
                    } else if (departureTime != null && departureTime.isBefore(earlyDepartureLimit)) {
                        status = "SALIDA_ANTICIPADA";
                    } else {
                        status = "PRESENTE";
                    }

                    return DailyRecordDTO.builder()
                            .userId(user.getId())
                            .fullName(user.getName() + " " + user.getLastName())
                            .entryTime(entryTime)
                            .departureTime(departureTime)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeeklyHistoryDTO> getMyWeeklyHistory(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<AttendanceRecord> records = attendanceRepository
                .findByUserIdAndDateBetween(userId, Timestamp.valueOf(start), Timestamp.valueOf(end));

        Map<LocalDate, List<AttendanceRecord>> recordsByDate = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate().toLocalDateTime().toLocalDate()
                ));

        // 4. Generar lista de días (últimos 7 días)
        LocalTime lateLimit = LocalTime.parse(attendanceEntry);
        LocalTime earlyDepartureLimit = LocalTime.parse(attendanceDeparture);

        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startDate.plusDays(i))
                .map(date -> {
                    List<AttendanceRecord> dayRecords = recordsByDate.getOrDefault(date, List.of());

                    AttendanceRecord entry = dayRecords.stream()
                            .filter(r -> r.getTypeAttendance() == AttendanceType.ENTRADA)
                            .findFirst()
                            .orElse(null);

                    AttendanceRecord departure = dayRecords.stream()
                            .filter(r -> r.getTypeAttendance() == AttendanceType.SALIDA)
                            .findFirst()
                            .orElse(null);

                    LocalTime entryTime = entry != null ? entry.getDate().toLocalDateTime().toLocalTime() : null;
                    LocalTime departureTime = departure != null ? departure.getDate().toLocalDateTime().toLocalTime() : null;

                    String status;
                    if (entryTime == null) {
                        status = "AUSENTE";
                    } else if (entryTime.isAfter(lateLimit)) {
                        status = "ATRASADO";
                    } else if (departureTime != null && departureTime.isBefore(earlyDepartureLimit)) {
                        status = "SALIDA_ANTICIPADA";
                    } else {
                        status = "PRESENTE";
                    }

                    return WeeklyHistoryDTO.builder()
                            .date(date)
                            .entryTime(entryTime)
                            .departureTime(departureTime)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
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
