# 🏦 Core Banking REST API

A backend system simulating core digital banking operations, built with **Java 17** and **Spring Boot 3**. The project integrates secure authentication with **JWT & Refresh Token Rotation**, electronic identity verification (**eKYC**), internal & interbank fund transfers (via a mock **NAPAS** gateway), online savings accounts, installment loans, and asynchronous balance notification processing powered by **Apache Kafka**.

---

## 🚀 Tech Stack

| Component | Technology / Library |
| :--- | :--- |
| **Language & Framework** | Java 17, Spring Boot 3.2.1 |
| **Security** | Spring Security 6, JJWT (0.12.3), BCrypt |
| **Database & ORM** | PostgreSQL 16, Spring Data JPA (Hibernate), HikariCP |
| **Message Broker** | Apache Kafka, Zookeeper, Kafka UI |
| **API Documentation** | SpringDoc OpenAPI 3 (Swagger UI 2.3.0) |
| **Internationalization (i18n)** | Spring MessageSource (`en`, `vi` via `Accept-Language` header) |
| **Build & Utilities** | Maven Wrapper (`mvnw`), Lombok, Jakarta Validation, Docker Compose |

---

## ✨ Key Features

1. **Authentication & Session Management**
   - User registration and login issuing **Access Tokens** (15 minutes) and **Refresh Tokens** (7 days).
   - **Refresh Token Rotation** with device (`User-Agent`) and IP address tracking, enforcing a maximum limit of concurrent active sessions per user.
2. **Electronic Know Your Customer (eKYC)**
   - Users submit KYC applications including ID number, front/back ID card photos, and a selfie (`multipart/form-data`).
   - Administrators (`ADMIN`) review, approve, or reject submissions with rejection reasons.
3. **Bank Account & Card Management**
   - Open checking/payment accounts, inquire balances, and update account statuses.
   - Issue debit/credit cards linked to bank accounts, view card details, and lock/unlock cards.
4. **Fund Transfers & Interbank Gateway Mock (NAPAS)**
   - **Internal Transfers**: Validates account status and available balance, executing atomic debit/credit operations (`@Transactional`).
   - **Interbank Transfers**: Integrates `NapasGatewayMockService` to resolve external recipient names and process interbank transfers.
   - **Asynchronous Notifications**: Publishes `TransactionEvent` messages to the Kafka `notification-events` topic to notify senders and recipients of balance changes.
5. **Beneficiary Directory**
   - Save, update, delete, and quickly look up frequent transfer recipients.
6. **Online Savings Accounts**
   - Open term-based savings accounts funded from checking accounts, view active savings, and close/settle savings accounts.
7. **Loans & Repayment Schedules**
   - **Customers**: Apply for loans, track application statuses, view installment repayment schedules, and pay individual installments.
   - **Administrators (`ADMIN`)**: View paginated loan applications, approve/reject pending requests, disburse approved loans (automatically generating installment schedules), and close active loans early.

---

## 📂 Project Structure

```text
banking/
├── src/main/java/com/me/base/
│   ├── config/          # CORS, Swagger/OpenAPI, Locale (i18n), and WebMVC configurations
│   ├── controller/      # REST API Controllers (Auth, User, Admin, Account, Card, Loan, KYC...)
│   ├── dto/             # Data Transfer Objects, BaseResponse wrapper, Kafka Events
│   ├── entity/          # JPA Entities mapped to database tables
│   ├── enums/           # Business status and type enumerations
│   ├── exception/       # Centralized error handling (GlobalExceptionHandler, BusinessException)
│   ├── listener/        # Kafka Consumers (NotificationListener)
│   ├── repository/      # Spring Data JPA Repositories
│   ├── security/        # Spring Security configuration & JwtAuthenticationFilter
│   ├── service/         # Business logic interfaces and implementations
│   └── util/            # Utility classes (JwtUtil, HashUtil)
├── src/main/resources/
│   ├── i18n/            # Localization bundles (messages_en.properties, messages_vi.properties)
│   ├── application.yaml # Shared configuration (Server port 8000, Kafka, JWT)
│   ├── application-dev.yaml  # Development profile configuration
│   └── application-prod.yaml # Production profile template
├── uploads/             # Local storage directory for uploaded eKYC images
├── docker-compose.yml   # Quick setup for PostgreSQL, Kafka, Zookeeper & Kafka UI
└── pom.xml              # Maven dependencies and build configuration
```

---

## ⚙️ Getting Started

### 1. Prerequisites
- **JDK 17** or higher
- **Docker** & **Docker Compose** (for PostgreSQL and Kafka)
- **Maven 3.8+** (or use the included `mvnw` / `mvnw.cmd` wrapper)

### 2. Start Infrastructure Services
From the project root directory, start PostgreSQL and the Apache Kafka cluster:

```bash
docker-compose up -d
```

Running services:
- **PostgreSQL**: `localhost:5432` (Database: `basedb` | User: `postgres` | Password: `123`)
- **Kafka Broker**: `localhost:9092`
- **Kafka UI**: [http://localhost:8081](http://localhost:8081) (Web UI for monitoring Kafka topics and messages)

### 3. Run the Application

**Windows (PowerShell / CMD):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

By default, the server starts with the `dev` profile at **`http://localhost:8000`**.

---

## 📖 API Documentation (Swagger / OpenAPI)

Once the application is running, explore and test the endpoints interactively via Swagger UI:

- **Swagger UI**: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8000/v3/api-docs](http://localhost:8000/v3/api-docs)

> **Testing Protected Endpoints in Swagger UI**:
> 1. Call `POST /api/auth/register` or `POST /api/auth/login` to obtain an `accessToken`.
> 2. Click the **Authorize** button at the top right and paste the `accessToken` into the Value field.
> 3. Optionally set the `Accept-Language` header parameter (`en` or `vi`) on any request for localized responses.

---

## 🔌 Core API Endpoints Overview

### Authentication & Users
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user account |
| `POST` | `/api/auth/login` | Authenticate user and receive Access/Refresh Tokens |
| `POST` | `/api/auth/refresh` | Refresh Access Token (with Refresh Token Rotation) |
| `POST` | `/api/auth/logout` | Logout and revoke the Refresh Token |
| `GET` | `/api/users/me` | Get the currently authenticated user's profile |
| `PUT` | `/api/admin/users/{id}/lock` | `[ADMIN]` Lock a customer user account |
| `PUT` | `/api/admin/users/{id}/unlock` | `[ADMIN]` Unlock a customer user account |

### eKYC Verification
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/kyc/submit` | Submit KYC documents (Front/Back ID card + Selfie) |
| `GET` | `/api/kyc/status` | Check current user's KYC verification status |
| `GET` | `/api/kyc/admin/pending` | `[ADMIN]` List all pending KYC submissions |
| `POST` | `/api/kyc/admin/approve/{kycId}` | `[ADMIN]` Approve a KYC submission |
| `POST` | `/api/kyc/admin/reject/{kycId}` | `[ADMIN]` Reject a KYC submission with a reason |

### Accounts, Cards, Transfers & Beneficiaries
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/accounts/user/{userId}` | Open a new bank account |
| `GET` | `/api/v1/accounts/user/{userId}` | Get all bank accounts for a user |
| `PUT` | `/api/v1/accounts/{id}/status` | Update bank account status |
| `POST` | `/api/v1/cards/user/{userId}` | Issue a new bank card |
| `GET` | `/api/v1/cards/account/{accountId}` | List cards linked to a bank account |
| `PUT` | `/api/v1/cards/{id}/status` | Lock / Unlock a card |
| `POST` | `/api/v1/transactions/transfer` | Execute an internal or interbank fund transfer |
| `GET/POST` | `/api/v1/beneficiaries` | List / Add saved beneficiaries |

> **Mock External Accounts for Interbank Transfer Testing (NAPAS Mock)**:
> - Bank Code `VCB` — Account Number: `123456` (`NGUYEN VAN A`)
> - Bank Code `TCB` — Account Number: `654321` (`TRAN THI B`)
> - Bank Code `BIDV` — Account Number: `111111` (`LE VAN C`)
> *(Leave `destinationBankCode` empty or pass `"MY_BANK"` for internal transfers)*

### Savings & Loans
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/savings` | Open a new savings account |
| `GET` | `/api/v1/savings` | List current user's savings accounts |
| `POST` | `/api/v1/savings/{id}/close` | Close / settle a savings account |
| `POST` | `/api/loans/apply` | Submit a new loan application |
| `GET` | `/api/loans` | List current user's loans |
| `GET` | `/api/loans/{loanId}/repayments` | View the installment repayment schedule for a loan |
| `POST` | `/api/loans/repayments/{repaymentId}/pay` | Pay a specific loan installment |
| `PUT` | `/api/admin/loans/{loanId}/review` | `[ADMIN]` Approve or reject a loan application |
| `PUT` | `/api/admin/loans/{loanId}/disburse` | `[ADMIN]` Disburse an approved loan & generate schedule |
| `PUT` | `/api/admin/loans/{loanId}/close` | `[ADMIN]` Close an active loan early |

---

## 🔒 Production Security Notes

- Switch `spring.profiles.active` to `prod` when deploying to production.
- Replace `jwt.secret` with a strong secret key (at least 64 characters) injected via environment variables.
- Never commit sensitive production credentials in `application-prod.yaml` to version control.
