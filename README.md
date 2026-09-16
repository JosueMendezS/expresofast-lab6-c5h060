# ExpresoFast — Laboratorio 6 (Parte II: Seguridad JWT, RBAC, DTOs y Auditoría)

**Curso:** IF0009 - Desarrollo de Software IV
**Ciclo:** II-2026
**Profesor:** Mag. Jonathan Granados C.
**Estudiante:** Josué Méndez Sanabria/Roger Dario Mora Piedra
**Carné:** c5h060/c5h489

## Requisitos de entorno

- Java 21
- Maven 3.9+
- Microsoft SQL Server Developer Edition + SSMS
- Navegador moderno (Chrome/Edge/Firefox) con DevTools

## Configuración de base de datos

1. Ejecutar `database/01_schema_lab5.sql` (esquema base del Laboratorio 5).
2. Ejecutar `database/02_schema_lab6_extension.sql` (tablas `Usuario`, `Rol`, `UsuarioRol`, `BitacoraEnvio`).
3. Ejecutar `database/03_data_seeds.sql` (roles e insumo de usuarios de prueba con contraseñas encriptadas en BCrypt).

## Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | Password123! | ROLE_ADMIN |
| operador1 | Password123! | ROLE_OPERADOR |
| conductor1 | Password123! | ROLE_CONDUCTOR |

## Ejecución

1. Copiar `backend/src/main/resources/application.properties.template` a `backend/src/main/resources/application.properties` y completar usuario/contraseña reales de SQL Server.
2. Levantar el backend: `cd backend && mvn spring-boot:run`
3. Abrir `frontend/login.html` en el navegador (o servirlo con Live Server).
