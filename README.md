# 🏭 Sistema de Gestión de Almacén — Empresa de Seguridad

Sistema web de gestión de almacén desarrollado para una empresa de seguridad privada con operaciones en múltiples sedes (Lima, Ica y Arequipa). Permite controlar el inventario de productos, movimientos de ingresos y salidas, gestión de trabajadores, proveedores y más, con soporte en tiempo real mediante WebSocket y un asistente de inteligencia artificial integrado.

---

## 🚀 Funcionalidades principales

- **Dashboard** con resumen de inventario, ingresos y salidas por sede
- **Gestión de Productos** con control de stock por sede (`tb_stock_sede`)
- **Ingresos y Salidas** de mercadería con registro de Kardex
- **Proveedores** — CRUD completo con filtros
- **Trabajadores** — CRUD con asignación de sede y rol
- **Exportación** de reportes en Excel y PDF (Apache POI + iText)
- **Chat IA** flotante con contexto real de la base de datos (Groq API + LLaMA 3)
- **Notificaciones en tiempo real** — 6 tipos de eventos vía WebSocket/STOMP
- **Chat grupal y directo** en tiempo real entre usuarios del sistema
- **Spring Security** con roles diferenciados (ADMIN / ALMACEN) y filtrado por sede
- **Control multi-sede** — ADMIN ve todas las sedes, ALMACEN solo la suya

---

## 🛠️ Tecnologías utilizadas

| Capa | Tecnología |
|------|------------|
| Backend | Java 17 + Spring Boot + Hibernate(JPA) |
| Frontend | Thymeleaf + Bootstrap |
| Base de datos | MySQL 8 |
| Seguridad | Spring Security + BCrypt |
| Tiempo real | WebSocket + STOMP |
| IA | Groq API (LLaMA 3) |
| Exportación | Apache POI (Excel) + iText (PDF) |
| Email | Brevo (Sendinblue) SMTP API |
| Consultas externas | ApisPeru (DNI / RUC) |

---

## 📁 Estructura del proyecto

```
almacen-seguridad/
├── src/
│   ├── main/
│   │   ├── java/com/seguridad/
│   │   │   ├── controller/       # Controladores MVC
│   │   │   ├── service/          # Lógica de negocio
│   │   │   ├── repository/       # Repositorios JPA
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── security/         # Spring Security + helpers
│   │   │   ├── scheduler/        # Alertas automáticas de stock
│   │   │   ├── dto/              # Chat grupal y directo
│   │   │   └── config/           # WebSocket, configuraciones
│   │   └── resources/
│   │       ├── templates/        # Vistas Thymeleaf
│   │       ├── static/           # CSS, JS, imágenes
│   │       └── application.properties.example
├── database/
│   └── bd_almacen_seguridad.sql  # Script completo de la BD
└── pom.xml
```

---

## ⚙️ Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/maxito29/almacen-seguridad.git
cd almacen-seguridad
```

### 2. Configurar la base de datos

Importar el script en MySQL:

```sql
mysql -u root -p < database/bd_almacen_seguridad.sql
```

### 3. Configurar las variables de entorno

Copiar el archivo de ejemplo y completar con tus credenciales:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Editar `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/almacen_seguridad
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

apisperu.token=tu_token_apisperu
groq.api.key=tu_api_key_groq
brevo.api.key=tu_api_key_brevo
app.mail.remitente=tu_correo@gmail.com
```

### 4. Ejecutar el proyecto

```bash
mvn spring-boot:run
```

Acceder en: `http://localhost:8080`

**Usuario por defecto:** `admin` / `admin123`

---

## 📸 Capturas de pantalla

**Login**
<img width="1358" alt="Login" src="https://github.com/user-attachments/assets/71f4fe6c-9351-40ff-8278-c28c0b55a70f" />

**Dashboard**
<img width="1366" alt="Dashboard" src="https://github.com/user-attachments/assets/7c165a25-fe3d-4426-8134-0cd2c5ed4536" />

**Productos**
<img width="1362" alt="Productos" src="https://github.com/user-attachments/assets/9b515a3a-4549-4ecf-a4ff-703a27567b17" />

**Ingresos**
<img width="1364" alt="Ingresos" src="https://github.com/user-attachments/assets/7d200a75-e99e-41ba-b2b1-b0a63fc15f4a" />

**Chat IA**
<img width="1364" alt="Chat IA" src="https://github.com/user-attachments/assets/26697fac-8147-4c0e-97b2-9092eb2095da" />

**Notificaciones**
<img width="1363" alt="Notificaciones" src="https://github.com/user-attachments/assets/ecbc509c-277a-4e76-90de-b690b5e7730f" />


---

## 👨‍💻 Autor

**Maximiliano López Avalos**  
**Lionel Lara Candela**
**Angeles Escalante Vargas**
**Jefferson Ucharima Parian**
Estudiantes de Computación e Informática — CIBERTEC  
[github.com/maxito29](https://github.com/maxito29)

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos para el curso de Lenguaje de Programación 2 (LP2) — CIBERTEC 2026.
