# CareerFit AI — Backend Service

A high-performance Java Spring Boot 3 REST service that parses PDF and DOCX resumes, extracts technical skills through boundary-aware dictionary matching, and persists the analysis results in MySQL.

---

## 📁 Package Architecture

```text
com.careerfitai/
├── CareerFitAiApplication.java     # Spring Boot application entry point
├── controller/
│   ├── ResumeController.java      # REST endpoints (/api/v1/resumes)
│   └── HealthController.java      # Health check endpoint (/api/v1/health)
├── service/
│   ├── ResumeService.java          # Orchestrates upload, extraction, and persistence
│   ├── FileValidationService.java  # Validates size, extensions, and magic bytes
│   ├── TextExtractionService.java  # PDFBox and POI document parsers
│   └── SkillExtractionService.java # Boundary regex skill extraction engine (80+ skills)
├── repository/
│   ├── ResumeRepository.java       # Spring Data JPA repository for Resume entity
│   └── ResumeSkillRepository.java  # Repository for normalized ResumeSkill entity
├── entity/
│   ├── Resume.java                 # JPA Entity (filename, type, size, text, timestamp)
│   └── ResumeSkill.java            # JPA Child Entity (resume_id, skill_name, category)
├── dto/
│   ├── ApiResponse.java            # Generic JSON response wrapper
│   ├── ResumeResponseDto.java      # Sanitized resume result payload
│   └── ErrorResponseDto.java       # Structured error representation
├── exception/
│   ├── GlobalExceptionHandler.java # @RestControllerAdvice centralized exception handler
│   ├── ResourceNotFoundException.java
│   ├── InvalidFileException.java
│   ├── EmptyFileException.java
│   └── FileProcessingException.java
└── config/
    └── CorsConfig.java             # Configurable CORS origin filter
```

---

## 🛠️ Prerequisites

- **Java**: Java 17 LTS or Java 21 LTS
- **Maven**: 3.8+
- **Database**: MySQL 8.0+

---

## 🗄️ Database Setup

### 1. Local MySQL Initialization
Launch your MySQL command-line or MySQL Workbench and run:

```sql
CREATE DATABASE careerfitai;
```

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, which automatically generates the `resumes` and `resume_skills` tables upon first run.

### 2. Environment Variables
You can configure your local credentials via environment variables or rely on `application.properties`:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:mysql://localhost:3306/careerfitai?createDatabaseIfNotExist=true` | MySQL JDBC connection URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `""` | Database password |
| `PORT` | `8080` | Web server listening port |
| `FRONTEND_URL` | `http://localhost:5500,https://*.vercel.app` | Allowed CORS origins |

---

## 🚀 Running Locally

```bash
# Navigate to backend directory
cd backend

# Build and execute with Maven
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## 🧪 Running Automated Tests

```bash
mvn test
```

The test suite runs 26 tests across all layers (Controllers, Services, File Parsing, Boundary Matching) using an isolated in-memory H2 database. No external MySQL connection is required to run tests.

---

## 📡 REST API Reference

Base Path: `/api/v1`

### 1. Upload & Analyze Resume
- **Endpoint**: `POST /api/v1/resumes/analyze`
- **Content-Type**: `multipart/form-data`
- **Request Field**: `file` (Binary PDF or DOCX file, max 5 MB)
- **Response**: `201 Created`

```json
{
  "success": true,
  "message": "Resume uploaded successfully",
  "data": {
    "id": 1,
    "filename": "alex_software_engineer.pdf",
    "fileType": "PDF",
    "skills": [
      "AWS",
      "Docker",
      "Hibernate",
      "Java",
      "Kubernetes",
      "MySQL",
      "React",
      "REST API",
      "Spring Boot"
    ],
    "skillCount": 9,
    "createdAt": "2026-10-14T00:30:00",
    "categorizedSkills": {
      "Programming Languages": ["Java"],
      "Backend": ["Hibernate", "REST API", "Spring Boot"],
      "Databases": ["MySQL"],
      "Cloud & DevOps": ["AWS", "Docker", "Kubernetes"],
      "Frontend": ["React"]
    }
  }
}
```

### 2. Retrieve Analysis by ID
- **Endpoint**: `GET /api/v1/resumes/{id}`
- **Response**: `200 OK`

### 3. Health Check
- **Endpoint**: `GET /api/v1/health`
- **Response**: `200 OK`
```json
{
  "status": "UP",
  "service": "CareerFit AI Backend",
  "version": "1.0.0",
  "timestamp": "2026-10-14T00:30:00"
}
```

---

## 🐳 Docker Build

```bash
# Build Docker image
docker build -t careerfitai-backend .

# Run container with environment variables
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/careerfitai" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="secret" \
  careerfitai-backend
```

---

## ☁️ Deploying to Render

1. Push your repository to GitHub.
2. Sign in to [Render](https://render.com).
3. Click **New +** → **Web Service**.
4. Connect your GitHub repository.
5. In **Root Directory**, enter `backend`.
6. Select **Docker** environment (or Maven environment with Java 21).
7. In **Environment Variables**, provide your external MySQL credentials:
   - `DB_URL`: `jdbc:mysql://<your-remote-host>:3306/<database>?useSSL=true&allowPublicKeyRetrieval=true`
   - `DB_USERNAME`: `<username>`
   - `DB_PASSWORD`: `<password>`
   - `FRONTEND_URL`: `https://<your-vercel-app>.vercel.app`
8. Click **Create Web Service**.
9. Once deployed, verify `https://<your-render-service>.onrender.com/api/v1/health`.
