package com.aacevedodev.attendancecontrolapi.repository;

import com.aacevedodev.attendancecontrolapi.model.AttendanceRecord;
import com.aacevedodev.attendancecontrolapi.model.User;
import com.aacevedodev.attendancecontrolapi.model.enums.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    List<AttendanceRecord> findByUserId(Integer userId);
    List<AttendanceRecord> findByUserIdAndDateBetween(Integer userId, Timestamp startDate, Timestamp endDate);
    List<AttendanceRecord> findByTypeAttendanceAndDateBetween(AttendanceType type, Timestamp startDate, Timestamp endDate);
    List<AttendanceRecord> findByUserIdAndTypeAttendanceAndDateBetween(Integer userId, AttendanceType type, Timestamp startDate, Timestamp endDate);
    boolean existsByUserIdAndTypeAttendanceAndDateBetween(Integer userId, AttendanceType type, Timestamp startDate, Timestamp endDate);
    Optional<AttendanceRecord> findTopByUserIdAndTypeAttendanceOrderByDateDesc(Integer userId, AttendanceType type);
    @Query("SELECT a FROM AttendanceRecord a " +
            "WHERE a.typeAttendance = :type " +
            "AND a.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByTypeAndDateRange(
            @Param("type") AttendanceType type,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );
    @Query("SELECT a FROM AttendanceRecord a " +
            "WHERE a.user.id = :userId " +
            "AND a.typeAttendance = :type " +
            "AND a.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByUserIdAndTypeAndDateRange(
            @Param("userId") Integer userId,
            @Param("type") AttendanceType type,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );
    @Query(value = "SELECT * FROM attendance_record", nativeQuery = true)
    List<AttendanceRecord> findAllIncludingDeletedUsers();
    @Query(value = "SELECT ar.* FROM attendance_record ar " +
            "JOIN user u ON ar.user_id = u.id " +
            "WHERE ar.type = 'ENTRADA' " +
            "AND ar.date BETWEEN :startDate AND :endDate " +
            "AND TIME(ar.date) > :horaLimite " +
            "AND u.deleted = false",
            nativeQuery = true)
    List<AttendanceRecord> findLateArrivals(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("horaLimite") String horaLimite
    );
    @Query(value = "SELECT ar.* FROM attendance_record ar " +
            "JOIN user u ON ar.user_id = u.id " +
            "WHERE ar.type = 'SALIDA' " +
            "AND ar.date BETWEEN :startDate AND :endDate " +
            "AND TIME(ar.date) < :horaLimite " +
            "AND u.deleted = false",
            nativeQuery = true)
    List<AttendanceRecord> findEarlyDepartures(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("horaLimite") String horaLimite
    );
    @Query(value = "SELECT u.* FROM user u " +
            "WHERE u.deleted = false " +
            "AND u.id NOT IN ( " +
            "    SELECT ar.user_id FROM attendance_record ar " +
            "    WHERE DATE(ar.date) = :fecha " +
            ")",
            nativeQuery = true)
    List<User> findAbsentUsers(@Param("fecha") String fecha);
}
