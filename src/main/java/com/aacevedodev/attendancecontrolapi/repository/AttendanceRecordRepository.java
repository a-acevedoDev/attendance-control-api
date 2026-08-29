package com.aacevedodev.attendancecontrolapi.repository;

import com.aacevedodev.attendancecontrolapi.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
}
