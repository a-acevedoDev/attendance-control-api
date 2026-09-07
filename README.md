# Attendance Control API

Sistema de control de asistencia para empleados, desarrollado con **Spring Boot 4** y **Spring Security con JWT**.

---

## Tecnologías

- Java 17
- Spring Boot 4.1.1
- Spring Security 7.1.1
- Spring Data JPA 4.1.1
- MySQL 8.0.46
- JWT (JJWT) 0.12.6
- Lombok 1.18.46
- SpringDoc OpenAPI 3.0.1

---

## Dependencias

- Spring Boot Starter Data JPA 4.1.1 -> spring-boot-starter-data-jpa
- Spring Boot Starter Security 4.1.1 -> spring-boot-starter-security
- Spring Boot Starter Web 4.1.1 -> spring-boot-starter-web
- Spring Boot Starter Validation 4.1.1 -> spring-boot-starter-validation
- MySQL Connector J 8.0.46 -> mysql-connector-j
- JJWT API 0.12.6 -> jjwt-api
- JJWT Impl 0.12.6 -> jjwt-impl
- JJWT Jackson 0.12.6 -> jjwt-jackson
- Lombok 1.18.46 -> lombok
- SpringDoc OpenAPI Starter WebMVC UI 3.0.1 -> springdoc-openapi-starter-webmvc-ui
- Spring Boot DevTools 4.1.1 -> spring-boot-devtools

---

#  ENDPOINTS DE LA API

---

## Autenticación (Público)

- `POST /api/auth/login` → Iniciar sesión y obtener token JWT  
- `POST /api/auth/logout` → Cerrar sesión

---

## Usuarios (Solo ADMIN)

- `POST /api/users` → Crear un nuevo usuario  
- `GET /api/users` → Listar usuarios (filtro por estado: activo, inactivo, todos)  
- `GET /api/users/{id}` → Obtener usuario por ID  
- `GET /api/users/paginated` → Listar usuarios con paginación  
- `GET /api/users/management` → Listar usuarios para gestión (con estado activo/inactivo)  
- `PUT /api/users/{id}` → Actualizar usuario  
- `DELETE /api/users/{id}` → Eliminar usuario (soft delete)

---

## Asistencia (ADMIN y EMPLOYEE)

- `POST /api/attendance/check-in` → Marcar entrada (usuario autenticado)  
- `POST /api/attendance/check-out` → Marcar salida (usuario autenticado)  
- `POST /api/attendance/register` → Registrar asistencia (con DTO)  
- `GET /api/attendance/has-checked-in-today` → Verificar si ya marcó entrada hoy  
- `GET /api/attendance/my-weekly-history` → Historial semanal del usuario autenticado

---

## Reportes y Dashboard (Solo ADMIN)

- `GET /api/attendance/dashboard` → Métricas del dashboard (presentes, atrasos, inasistencias)  
- `GET /api/attendance/today` → Registros del día (todos los usuarios)  
- `GET /api/attendance/reports/late-arrivals` → Reporte de empleados que llegaron tarde  
- `GET /api/attendance/reports/early-departures` → Reporte de empleados que salieron antes de tiempo  
- `GET /api/attendance/reports/absenteeism` → Reporte de empleados que no asistieron  
- `GET /api/attendance/user/{userId}` → Historial de asistencia de un usuario específico  
- `GET /api/attendance/user/{userId}/range` → Historial de asistencia por rango de fechas

---

## Parámetros de Consulta

- `estado` → Valores: `activo`, `inactivo`, `todos`  
  Ejemplo: `GET /api/users?estado=activo`

- `date` → Formato: `YYYY-MM-DD`  
  Ejemplo: `GET /api/attendance/reports/late-arrivals?date=2026-09-01`

- `startDate` / `endDate` → Formato: `YYYY-MM-DD`  
  Ejemplo: `GET /api/attendance/user/1/range?startDate=2026-08-01&endDate=2026-08-31`

---

##  Headers Requeridos (Para endpoints con token)

- `Authorization: Bearer <token>`
