# AquaPulse

**Smart Rainwater Harvesting Monitoring System**

AquaPulse is a full-stack platform that helps residential societies digitally track and manage their rainwater harvesting (RWH) infrastructure — replacing manual, untracked systems with real-time monitoring of water collection, storage levels, and maintenance schedules. Societies can also hold video and voice meetings inside the platform to discuss maintenance, audits, and water-saving decisions.

Frontend repository: [aquapulse-frontend](https://github.com/Sumeet-Y1/aquapulse-frontend)

## Problem Statement

Many residential societies have rainwater harvesting infrastructure but lack any digital mechanism to monitor collection efficiency, storage levels, and maintenance status — leading to underutilization of a valuable water resource.

## Features

- **Society & Unit Management** — Register societies and their RWH units with tank capacity, rooftop area, and install details
- **Invite Codes** — Residents join societies through invite codes (with QR support)
- **Water Reading Logs** — Track daily/periodic water collection, storage levels, and rainfall data
- **Maintenance Tracking** — Log inspections, cleaning schedules, and get alerts for overdue maintenance
- **Analytics Dashboard** — Visualize collection trends, storage utilization, and system efficiency over time
- **Weather & AI Insights** — Live rainfall data and AI-generated performance insights
- **Society Meetings (Video & Voice)** — Admins schedule meetings; residents join in-app through society-restricted rooms, with no separate video-service login
- **Screen Sharing** — Present dashboards and reports during meetings; sharing sessions are logged
- **Meeting Attendance** — Join and leave times are recorded per participant
- **Authentication** — Email/password and Google Sign-In, with OTP email verification
- **Role-Based Access** — Separate permissions for society admins and residents

## Tech Stack

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Maven
- Docker

**Real-Time Communication**
- JaaS (Jitsi as a Service) — WebRTC video, voice, and screen sharing
- Backend-signed RS256 JWTs: admins join as moderators, residents as participants

**Frontend** (separate repository)
- React, TypeScript, Vite, Tailwind CSS

## Project Structure

```
aquapulse-backend/
├── controller/    → REST API endpoints
├── service/       → Business logic
├── repository/    → JPA repositories
├── model/entity/  → Database entities
├── dto/           → Data transfer objects
├── security/      → JWT & auth config
├── exception/     → Global exception handling
└── config/        → App configuration
```

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Maven (or use the included `mvnw` wrapper)
- (Optional) A free [JaaS](https://jaas.8x8.vc) account for meetings

### Setup
```bash
git clone https://github.com/Sumeet-Y1/aquapulse.git
cd aquapulse/aquapulse-backend
./mvnw spring-boot:run
```

Configure your PostgreSQL credentials in `src/main/resources/application.properties` before running. For local development, put your settings in `application-local.properties` (keep it out of git) and run with the `local` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Meetings configuration (JaaS)

Meetings use backend-signed JWTs. Set these three values as environment variables (or in your local properties file):

| Property | Env variable | Description |
|---|---|---|
| `jaas.app-id` | `JAAS_APP_ID` | Your JaaS App ID (`vpaas-magic-cookie-...`) |
| `jaas.key-id` | `JAAS_KEY_ID` | The API key ID (`<app-id>/<suffix>`) |
| `jaas.private-key` | `JAAS_PRIVATE_KEY` | Your RSA private key (PKCS#8 PEM), **Base64-encoded** into one line |

Encode the key on Windows PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("path\to\your.pk"))
```

Never commit the private key. If these values are not set, the backend falls back to the public `meet.jit.si` server, which asks the first person to log in and does not support moderator tokens. For private society meetings, turn off "Allow meeting participants to join unauthenticated" in the JaaS console once everything works.

## Core Entities

| Entity | Description |
|---|---|
| `User` | Admin/resident accounts with role-based access |
| `Society` | Housing society details |
| `RWHUnit` | Individual rainwater harvesting units per society |
| `WaterReading` | Periodic water collection & storage readings |
| `MaintenanceLog` | Maintenance history and scheduling |
| `Meeting` | Scheduled society meetings with room, type, and status (Scheduled, Live, Ended, Cancelled) |
| `MeetingParticipant` | Attendance record (join and leave times) for each meeting |
| `ScreenShareLog` | Records of who shared their screen and when |

## Meeting API

All endpoints require a valid JWT. Only members of the meeting's society can access it.

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/meetings` | Schedule a meeting | Admin |
| GET | `/api/meetings/society/{societyId}` | List a society's meetings | Admin, Resident |
| GET | `/api/meetings/{id}` | Meeting details | Admin, Resident |
| POST | `/api/meetings/{id}/join` | Join a meeting; returns room name, `moderator` flag, and a signed `jwt` | Admin, Resident |
| POST | `/api/meetings/{id}/leave` | Record that the user left | Admin, Resident |
| POST | `/api/meetings/{id}/start` | Mark a scheduled meeting live | Admin |
| POST | `/api/meetings/{id}/end` | End a live meeting and close attendance | Admin |
| POST | `/api/meetings/{id}/screenshare` | Log screen-share start/stop (`{"sharing": true}`) | Admin, Resident |
| DELETE | `/api/meetings/{id}` | Cancel a scheduled meeting | Admin |

**Rules**
- An admin joining a scheduled meeting starts it automatically.
- Residents can join only once the meeting is live (otherwise `409`).
- Users outside the society get `403`. Ended or cancelled meetings return `409`.
- Room names are random UUIDs, so they can't be guessed.
- Screen sharing can be switched off per meeting. This is currently enforced in the client toolbar, not in the token.

## License

This project is developed for academic purposes.