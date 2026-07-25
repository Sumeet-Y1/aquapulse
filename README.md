# 🌊 AquaPulse

**Smart Rainwater Harvesting Monitoring System**

AquaPulse is a full-stack platform that helps residential societies digitally track and manage their rainwater harvesting (RWH) infrastructure — replacing manual, untracked systems with real-time monitoring of water collection, storage levels, and maintenance schedules.

## 📌 Problem Statement

Many residential societies have rainwater harvesting infrastructure but lack any digital mechanism to monitor collection efficiency, storage levels, and maintenance status — leading to underutilization of a valuable water resource.

## ✨ Features

- 🏢 **Society & Unit Management** — Register societies and their RWH units with tank capacity, rooftop area, and install details
- 💧 **Water Reading Logs** — Track daily/periodic water collection, storage levels, and rainfall data
- 🔧 **Maintenance Tracking** — Log inspections, cleaning schedules, and get alerts for overdue maintenance
- 📊 **Analytics Dashboard** — Visualize collection trends, storage utilization, and system efficiency over time
- 🔐 **Role-Based Access** — Separate views for society admins and residents
- 🔔 **Smart Alerts** — Notifications for low storage, overdue maintenance, and overflow risk

## 🛠️ Tech Stack

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Maven

**Frontend**
- React.js
- Chart.js / Recharts (for analytics visualization)

## 📁 Project Structure

aquapulse-backend/
├── controller/ → REST API endpoints
├── service/ → Business logic
├── repository/ → JPA repositories
├── model/entity/ → Database entities
├── dto/ → Data transfer objects
├── security/ → JWT & auth config
├── exception/ → Global exception handling
└── config/ → App configuration


## 🚀 Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Maven (or use the included `mvnw` wrapper)

### Setup
```bash
git clone https://github.com/Sumeet-Y1/aquapulse.git
cd aquapulse/aquapulse-backend
./mvnw spring-boot:run
```

Configure your PostgreSQL credentials in `src/main/resources/application.properties` before running.

## 📖 Core Entities

| Entity | Description |
|---|---|
| `User` | Admin/resident accounts with role-based access |
| `Society` | Housing society details |
| `RWHUnit` | Individual rainwater harvesting units per society |
| `WaterReading` | Periodic water collection & storage readings |
| `MaintenanceLog` | Maintenance history and scheduling |

## 🎓 Academic Project

Developed as part of **R-2025 S.Y B.Tech Information Technology** at Shree L.R. Tiwari College of Engineering.

## 📄 License

This project is for academic purposes.