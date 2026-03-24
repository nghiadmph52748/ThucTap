# ThucTapProject

Spring Boot REST API (Users / Projects / Tasks) with JWT authentication and Swagger UI.

## 1) Requirements

- Java 21+
- Maven (project includes `mvnw` / `mvnw.cmd`)
- SQL Server (dev profile uses a local SQLServer connection)

## 2) Profiles (dev / prod)

This project uses Spring profiles:

- **dev** (default): local development settings
- **prod**: production settings (secrets pulled from environment variables)

Config files:

- `src/main/resources/application.properties` (common + `spring.profiles.default=dev`)
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

### Run with dev profile (default)

```powershell
cd "D:\Luu Tam\thucTap\week3\ThucTapProject"
.\mvnw.cmd spring-boot:run
```

Or explicitly:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run with prod profile

Provide environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION` (optional, default: 3600000)

Example (PowerShell):

```powershell
$env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=THUCTAP;encrypt=true;trustServerCertificate=true;"
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your_base64_secret"
$env:JWT_EXPIRATION="3600000"

java -jar .\target\ThucTapProject-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 3) Build JAR

```powershell
cd "D:\Luu Tam\thucTap\week3\ThucTapProject"
.\mvnw.cmd clean package
```

Output:

- `target/ThucTapProject-0.0.1-SNAPSHOT.jar`

## 4) Run JAR locally

```powershell
cd "D:\Luu Tam\thucTap\week3\ThucTapProject"
java -jar .\target\ThucTapProject-0.0.1-SNAPSHOT.jar
```

> By default it will use `dev` profile because `spring.profiles.default=dev`.

## 5) Swagger UI (API documentation & testing)

After starting the app:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### How to test JWT-protected APIs in Swagger

1. Call **Auth -> POST /api/auth/login** to get token.
2. In Swagger UI click **Authorize**.
3. Enter in `bearerAuth`:
   - `Bearer <YOUR_TOKEN>`
4. Call APIs normally.

Manual evidence checklist: see `SWAGGER_TEST_EVIDENCE.md`.

Notes:

- `/api/auth/**` is public.
- Most endpoints require role `MANAGER` (see `SecurityConfig`).

## 6) API Endpoints (high level)

- Auth
  - `POST /api/auth/login`
  - `POST /api/auth/register`
- Projects
  - `GET /api/projects/list`
  - `POST /api/projects/add` (MANAGER)
- Tasks
  - `GET /api/tasks/list` (MANAGER)
  - `GET /api/tasks/list-by-user/{id}` (MANAGER)
  - `GET /api/tasks/list-by-project/{id}` (MANAGER)
  - `GET /api/tasks/detail/{id}` (authenticated; USER can only view own tasks)
  - `GET /api/tasks/my` (authenticated)
  - `POST /api/tasks/add` (MANAGER)
  - `PUT /api/tasks/update/{id}` (MANAGER)
  - `DELETE /api/tasks/delete/{id}` (MANAGER)
  - `PUT /api/tasks/assign/{taskId}/{userId}` (MANAGER)
  - `PUT /api/tasks/change-status/{taskId}/{status}` (MANAGER)
- Users (MANAGER)
  - `GET /api/users/list`
  - `GET /api/users/detail/{id}`
  - `POST /api/users/add`
  - `PUT /api/users/update/{id}`
  - `DELETE /api/users/delete/{id}`

## 7) How to run tests

Run all tests:

```powershell
cd "D:\Luu Tam\thucTap\week3\ThucTapProject"
.\mvnw.cmd test
```

Run only unit tests for TaskService:

```powershell
.\mvnw.cmd -Dtest=TaskServiceTest test
```

## 8) Evidence (Swagger testing)

Gợi ý cách lưu evidence cho report:

- Chụp màn hình Swagger UI sau khi:
  - Login thành công và copy JWT token
  - Authorize thành công
  - Gọi thử 1-2 API (VD: `GET /api/projects/list`, `GET /api/tasks/my`)

> Bạn có thể lưu ảnh vào thư mục `docs/` (không bắt buộc).


