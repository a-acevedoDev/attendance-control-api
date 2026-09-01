package com.aacevedodev.attendancecontrolapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SoftDelete
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(length = 12)
    @Size(min = 12, max = 12, message = "El rut debe tener 12 caracteres.")
    private String rut;

    @NotBlank
    @Column(length = 100)
    @Size(max = 100, message = "El nombre debe tener maximo 100 caracteres.")
    private String name;

    @NotBlank
    @Column(length = 100)
    @Size(max = 100, message = "El apellido debe tener maximo 100 caracteres.")
    private  String lastName;

    @Column(length = 20)
    @Size(max = 20, message = "El telefono debe tener maximo 20 caracteres.")
    private String phone;

    @Size(max = 255, message = "La dirección debe tener maximo 255 caracteres.")
    private String address;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Timestamp createAt;

    @Column(name = "update_at")
    private Timestamp updateAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Credential credential;

    @OneToMany(mappedBy = "user", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE}
    )
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
