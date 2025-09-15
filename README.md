# Booking System (Spring Boot + PostgreSQL + Redis)

A booking system built with **Java 21**, **Spring Boot**, **Spring Data JPA**, **Liquibase**, and **Gradle**.  
Users can add accommodation units, search with filters, book units, simulate payments, and cancel bookings.  
Statistics on available units are cached in **Redis**.

---

## Features
- Add new Units with properties:
    - Number of rooms
    - Type of accommodation (`HOME`, `FLAT`, `APARTMENTS`)
    - Unit’s floor
    - Description and cost (system automatically adds +15% markup)
- Search Units with filters:
    - Type, event type, date range, min/max cost
    - Supports sorting and pagination
- Booking lifecycle:
    - Book a unit (becomes unavailable)
    - Cancel booking
    - Simulate payment (must pay within **15 minutes**, otherwise auto-cancelled)
- Statistics:
    - Cached number of available units
    - Cache updated on every change (with Redis persistence)
- API documentation via **Swagger/OpenAPI**

---

## Tech Stack
- **Java 21**
- **Spring Boot 3**
- **Spring Web, Spring Data JPA**
- **PostgreSQL** (via Docker)
- **Redis** (via Docker)
- **Liquibase** (DB migrations + sample data)
- **Gradle** (build tool)
- **JUnit 5, Mockito, Spring Test, Testcontainers** (tests)
- **OpenAPI/Swagger** (API docs)

---

## Prerequisites
- **Java 21**
- **Gradle**
- **Docker + Docker Compose**

---

## Setup & Run

### 1. Start dependencies (PostgreSQL + Redis)
```bash
docker-compose up -d
```

### 2. Run the application
```bash
./gradlew bootRun
```
or from IDE run BookingApplication

### 3. Access the app
Swagger UI: http://localhost:8080/swagger-ui/index.html
API base URL: http://localhost:8080/api

### 4. Example API Endpoints

Units

POST /api/unit → Create new Unit

GET /api/unit/search → Search units with filters

GET /api/unit/total → Get cached total available units

Bookings

POST /api/booking → Create booking

PATCH /api/booking/pay/{id}?amount=100 → Simulate payment

PATCH /api/booking/cancel/{id} → Cancel booking
