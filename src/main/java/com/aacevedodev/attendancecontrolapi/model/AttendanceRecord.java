package com.aacevedodev.attendancecontrolapi.model;

import com.aacevedodev.attendancecontrolapi.model.enums.TypeAttendance;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "attendance_record")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TypeAttendance typeAttendance;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp date;

    @Column(name = "ip_origen", length = 45)
    @Size(max = 45, message = "La ip no puede tener mas de 45 digitos.")
    private String ipOrigin;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
