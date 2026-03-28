# AI-Powered Task Flow Tracker

 A smart backend system that generates **personalized day-wise learning plans** using Groq AI, tracks your daily progress, and keeps you on path to complete your learning goals.

---

##  Features

- JWT Authentication** — Secure login with BCrypt password hashing
- OTP Email Verification** — New users verify via email OTP before login
- Groq AI Integration** — Auto-generates day-wise learning plans (LLaMA 3.3)
- Progress Tracker** — Real-time % completion as you mark daily topics done
- Resend OTP** — Resend if OTP expires
- Forgot Password** — OTP-based password reset flow
- Swagger UI** — Fully documented REST API

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT |
| AI | Groq API (LLaMA 3.3-70b) |
| Database | PostgreSQL |
| ORM | Hibernate, JPA |
| Email | Mailtrap SMTP |
| Docs | Swagger UI (SpringDoc) |
| Build | Maven |

---

## Database Schema
```
users
  └── id, username, password, email, is_verified

tasks
  └── id, title, description, priority, status, deadline, created_at, user_id

otp_store
  └── id, email, otp_code, expiry_time

checklist_items
  └── id, day_number, topic, is_done, task_id
```

---

## Getting Started

- Prerequisites
- Java 17+
- PostgreSQL
- Maven
- Groq API Key (free at [console.groq.com](https://console.groq.com))
- Mailtrap account (free at [mailtrap.io](https://mailtrap.io))

### Setup

 1. Clone the repository**
```bash
git clone https://github.com/Varshadarshan/AI-Task-Manager.git
cd AI-Task-Manager
```

2. Create PostgreSQL database**
```sql
CREATE DATABASE taskmanager;
```

3. Configure application.properties**
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Fill in your database credentials, Mailtrap SMTP, and Groq API key.

4. Run the application**
```bash
./mvnw spring-boot:run
```

5. Access Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register + sends OTP to email |
| POST | `/auth/verify-otp?email=&otp=` | Verify OTP → activate account |
| POST | `/auth/login` | Login → returns JWT token |
| POST | `/auth/resend-otp?email=` | Resend OTP |
| POST | `/auth/forgot-password?email=` | Send password reset OTP |
| POST | `/auth/reset-password?email=&otp=&newPassword=` | Reset password |

Tasks (JWT Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tasks` | Create task + AI generates learning plan |
| GET | `/tasks` | Get all tasks for logged-in user |
| GET | `/tasks/{id}` | Get task by ID |
| PUT | `/tasks/{id}` | Update task |
| DELETE | `/tasks/{id}` | Delete task |
| GET | `/tasks/paged` | Get paginated tasks |

AI Learning Plan (JWT Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tasks/{id}/progress` | Get progress % + full checklist |
| PATCH | `/tasks/{taskId}/checklist/{itemId}?isDone=true` | Mark day as done ✅ |

---

Complete User Flow
```
Register → OTP Email → Verify OTP → Login → JWT Token
    ↓
Create Task (title: "Java", deadline: 30 days)
    ↓
Groq AI generates 30-day plan:
  Day 1: Introduction to Java & Setup
  Day 2: Variables and Data Types
  Day 3: OOP Concepts
  ... up to Day 30
    ↓
Mark Day 1 done → 3.3% complete 
Mark Day 15 done → 50% complete 
Mark Day 30 done → 100% complete 
```

---

Progress Response Example
```json
{
  "taskId": 1,
  "title": "Java",
  "totalDays": 30,
  "completedDays": 8,
  "remainingDays": 22,
  "progressPercentage": 26.7,
  "daysUntilDeadline": 19,
  "statusMessage": " You're making solid progress!",
  "checklist": [
    { "dayNumber": 1, "topic": "Introduction to Java & Setup", "isDone": true },
    { "dayNumber": 2, "topic": "Variables and Data Types", "isDone": false }
  ]
}
```

---

Environment Variables

See `application.properties.example` for all required configuration:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.username=your_username
spring.datasource.password=your_password

# Mailtrap (OTP emails)
spring.mail.username=your_mailtrap_username
spring.mail.password=your_mailtrap_password

# Groq AI
groq.api.key=your_groq_api_key
```
