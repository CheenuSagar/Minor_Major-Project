# NextStore - POS & Retail Inventory Management System
> **Smarter Billing, Faster Retail**

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.5](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0%2B-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

NextStore is an enterprise-grade Point of Sale (POS) and Inventory Management System engineered for supermarkets, electronics showrooms, and retail stores. It features real-time inventory synchronization, dynamic UPI QR billing, camera barcode scanning, role-based workflows, and AI stock forecasting.

---

## Key Features

- **Multi-Role Workspaces**: Switch instantly between **Customer**, **Cashier**, **Store Manager**, and **Store Owner** views.
- **Dynamic UPI QR Billing**: Instant on-screen UPI QR codes (`upi://`) for Google Pay, PhonePe, Paytm, and BHIM.
- **Camera Barcode Scanner**: Built-in webcam barcode reader with Web Audio API synthetic beeping.
- **Digital Receipts**: Instant WhatsApp and SMS receipt dispatch with formatted GST breakdown.
- **Promotional Coupons**: Real-time coupon validator supporting flat and percentage discounts (`FLAT50`, `SAVE10`, `NEXTSTORE20`, `SUPERMCA`).
- **AI Stock Forecaster**: Intelligent sales velocity tracking and runout estimation alerts.
- **Relational Persistence**: High-throughput MySQL 8.0 database backend with HikariCP connection pooling.
- **Security & RBAC**: Stateless JWT authentication, BCrypt password hashing, and Bucket4j rate limiting.

---

## Quick Start (Windows)

### Prerequisites
- **Java 17 LTS** or higher
- **MySQL Server 8.0** running on port `3306`

### 1-Click Startup
Double click `run-mysql.bat` in the project root:
```cmd
run-mysql.bat
```

Open your browser at:
👉 **`http://localhost:8080`**

### Default Accounts
| Role | Username | Password |
| :--- | :--- | :--- |
| **Store Manager** | `manager` | `Manager@123` |
| **Walk-in Customer** | `customer` | `Customer@123` |

---

## Documentation
- [System Architecture](docs/ARCHITECTURE.md)
- [REST API Reference](docs/API_DOCUMENTATION.md)
- [Database Schema Dictionary](docs/DATABASE_SCHEMA.md)
- [Hardware Integration Guide](docs/HARDWARE_INTEGRATION.md)
- [MySQL Setup Guide](docs/MYSQL_SETUP_GUIDE.md)
- [Viva & Technical FAQs](docs/PROJECT_VIVA_QA.md)
- [User Manual](docs/USER_MANUAL.md)

---

## Author
Developed by **Cheenu Sagar** (Minor/Major Project).
