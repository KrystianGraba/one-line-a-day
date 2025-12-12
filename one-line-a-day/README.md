# One Line a Day - Backend API

## Overview
The backend service for One Line a Day, built with Spring Boot 3.2. It provides RESTful endpoints for user management, authentication, and journal entry synchronization.

## Tech Stack
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security + JWT
- **Database**: H2 (Dev) / PostgreSQL (Prod)
- **Migration**: Flyway
- **Build Tool**: Maven

## Key Features
- **Stateless Authentication**: JWT-based security.
- **Database Migrations**: Automated schema management with Flyway.
- **Global Error Handling**: Centralized exception advice for consistent API error responses.

## Configuration
Properties are defined in `src/main/resources/application.properties`.
- Default port: `8080`
- Default DB: H2 In-Memory (console enabled at `/h2-console`)

## How to Run

### Using Maven Wrapper
```bash
./mvnw spring-boot:run
```

### Using IDE
Import as a Maven project in IntelliJ IDEA or Eclipse and run the `OneLineADayApplication` class.

## API Endpoints
- **POST** `/auth/register` - Create account
- **POST** `/auth/login` - Get JWT
- **GET** `/api/journal/sync` - Get all entries (sync)
- **POST** `/api/journal` - Save an entry
- **GET** `/api/journal/search?q=...` - Search entries
- **GET** `/api/journal/stats` - Get user statistics

## Database
To use PostgreSQL instead of H2, update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/journaldb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```
