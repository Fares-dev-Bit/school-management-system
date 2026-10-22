# School Management System

A web-based school management system for candidate registration and tracking. Runs locally or on a network.

## Features

- Candidate registration and management
- Multi-stage tracking (Beginner, Code, Creneau, Circulation)
- Category management (A1, A, B)
- Search and filtering capabilities
- CSV import/export
- Role-based access control (Admin, Spectator)
- Dark/Light theme support
- Multi-language support (English/French)
- Responsive design for desktop and mobile

## Technology Stack

- **Backend**: Spring Boot 4.1.0, Java 17
- **Database**: H2 (file-based, embedded)
- **Security**: Spring Security with BCrypt password encoding
- **Frontend**: Thymeleaf, vanilla JavaScript (ES6+), CSS Custom Properties
- **Build**: Maven

## Prerequisites

- Java 17+
- Maven 3.8+ (or use included Maven wrapper)

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/school-management-system.git
cd school-management-system
```

### 2. Configure environment variables
```bash
cp .env.example .env
# Edit .env with your secure passwords and configuration
```

**Required variables:**
- `APP_ADMIN_PASSWORD` - Strong password for admin user
- `APP_SPECTATOR_PASSWORD` - Strong password for spectator user
- `SPRING_DATASOURCE_PASSWORD` - Database password

### 3. Run the application
```bash
# Using Maven wrapper (Linux/macOS)
./mvnw spring-boot:run

# Using Maven wrapper (Windows)
mvnw.cmd spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/school-management-system-0.0.1-SNAPSHOT.jar
```

### 4. Access the application
- **Web UI**: http://localhost:8080/app
- **Login**: http://localhost:8080/login
- **API Base**: http://localhost:8080/api/v1/candidates

### Default Users (after first run with env vars set)
- **Admin**: username `admin`, password from `APP_ADMIN_PASSWORD`
- **Spectator**: username `user`, password from `APP_SPECTATOR_PASSWORD`

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_NAME` | Application name (internal) | `school-management-system` |
| `APP_DISPLAY_NAME` | Display name in UI | `School Management System` |
| `APP_SUBTITLE` | Subtitle in UI | `Candidate Registry` |
| `APP_VENDOR` | Vendor name for installer | `School Management System` |
| `SERVER_PORT` | HTTP port | `8080` |
| `SERVER_ADDRESS` | Bind address | `0.0.0.0` |
| `SPRING_DATASOURCE_URL` | H2 database URL | `jdbc:h2:file:${HOME}/Documents/school-management/candidates;AUTO_SERVER=TRUE` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | *(required)* |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode | `update` |
| `SPRING_H2_CONSOLE_ENABLED` | Enable H2 web console | `false` |
| `APP_ADMIN_PASSWORD` | Admin user password | *(required)* |
| `APP_SPECTATOR_PASSWORD` | Spectator user password | *(required)* |
| `APP_DATA_DIR` | Data directory under user home | `Documents/school-management` |

### Database Location

By default, the H2 database files are stored in:
- Linux/macOS: `~/Documents/school-management/candidates.mv.db`
- Windows: `%USERPROFILE%\Documents\school-management\candidates.mv.db`

Override with `APP_DATA_DIR` or `SPRING_DATASOURCE_URL`.

## Building for Distribution

### Windows Installer
```bash
# Requires JDK with jpackage (JDK 17+)
./build-windows.bat
```
Creates `dist/SchoolManagementSystem-1.0.exe`

### JAR File
```bash
./mvnw clean package
```
Output: `target/school-management-system-0.0.1-SNAPSHOT.jar`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/candidates` | List all candidates |
| GET | `/api/v1/candidates/{id}` | Get candidate by ID |
| POST | `/api/v1/candidates` | Create candidate |
| POST | `/api/v1/candidates/batch` | Create multiple candidates |
| PUT | `/api/v1/candidates/{id}` | Update candidate |
| PUT | `/api/v1/candidates/batch` | Batch update candidates |
| DELETE | `/api/v1/candidates/{id}` | Delete candidate |
| DELETE | `/api/v1/candidates/batch` | Delete multiple candidates |
| GET | `/api/v1/candidates/search` | Search/filter candidates |
| GET | `/api/v1/network-info` | Get server IP/port |
| POST | `/api/v1/shutdown` | Graceful shutdown (admin only) |

See [BACKEND-API.md](BACKEND-API.md) for detailed request/response examples.

## Project Structure

```
src/main/
├── java/com/app/demo/
│   ├── DemoApplication.java           # Main entry point
│   ├── configuration/                 # Config classes
│   │   ├── AppProperties.java         # App metadata config
│   │   ├── GlobalModelAttributes.java # Global template variables
│   │   ├── SecurityConfig.java        # Security configuration
│   │   └── UserDataSeeder.java        # Default user creation
│   ├── controllers/                   # REST & MVC controllers
│   ├── entity/                        # JPA entities
│   ├── repo/                          # Spring Data repositories
│   └── service/                       # Business logic
└── resources/
    ├── templates/                     # Thymeleaf templates
    │   ├── index.html                 # Main dashboard
    │   └── login.html                 # Login page
    ├── static/                        # Static assets
    └── application.yaml               # Configuration
```

## Security

- All endpoints require authentication except static resources and login
- Passwords are BCrypt encoded
- Session-based authentication with secure defaults
- CSRF disabled for API endpoints (stateless)
- Role-based authorization:
  - `ADMIN`: Full CRUD access, shutdown
  - `SPECTATOR`: Read-only access

## License

MIT License - see [LICENSE](LICENSE) for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## Support

For issues and feature requests, please use the [GitHub Issues](https://github.com/yourusername/school-management-system/issues) page.