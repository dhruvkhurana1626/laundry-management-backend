<div align="center">

# 🧺 Laundry Management Backend

**Multi-shop laundry management REST API with JWT auth, per-seller pricing, order tracking and a live dashboard**

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT%20%2B%20Refresh-black?logo=jsonwebtokens)
![AWS S3](https://img.shields.io/badge/Storage-AWS%20S3-FF9900?logo=amazons3&logoColor=white)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-85EA2D?logo=swagger&logoColor=black)

</div>

---

## 📌 Overview

A backend for laundry shop owners (**sellers**) to run their business online. Each seller gets a private workspace with their own **price list, customer orders, profile and monthly dashboard**. Data of one shop is never visible to another.

**What a shop owner can do:**
register → set prices per garment → create orders for walk-in customers → track them until delivered → see monthly revenue at a glance.

---

## ✨ Features

| | Feature |
|---|---|
| 🔐 | **JWT access + refresh tokens** (15-min access token, 30-day refresh token stored in DB) |
| 🚪 | **Logout and token refresh**; refresh tokens are revoked on logout and password change |
| 🔑 | **Forgot / reset / change password** with a 15-minute email reset link |
| 🛡️ | **Rate limiting** on login and forgot-password using **Bucket4j** (per email + IP) |
| 👕 | **Per-seller pricing**: default price set for all 7 garment types at signup, editable anytime |
| 🧾 | **Orders**: multiple garments per order, bill auto-calculated from the seller's own prices |
| 🔍 | **Search & filters**: by order ID, status, customer name / phone / email, last N days |
| 📄 | **Pagination**: 10 per page, newest first, DB-level filtering using JPA Specifications |
| 📬 | **Delivery email** sent asynchronously when an order is marked delivered |
| 📊 | **Dashboard**: this month's orders, revenue and orders per status |
| 🖼️ | **Profile & photo**: business details, profile image uploaded to **AWS S3** |
| 🧹 | **Scheduled cleanup** of expired reset tokens (hourly) |
| 🧯 | **Global exception handling** with clean error responses |
| 📖 | **Swagger / OpenAPI** docs, plus a health-check endpoint |

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.4, Spring MVC |
| Security | Spring Security, JWT (jjwt 0.12.6), BCrypt, CORS |
| Database | MySQL, Spring Data JPA (Hibernate), JPA Specifications |
| Rate limiting | Bucket4j |
| File storage | AWS SDK v2 (S3) |
| Email | Spring Mail (Gmail SMTP) |
| API docs | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Utilities | Lombok, Jakarta Validation |

---

## 🏗️ Architecture

```mermaid
flowchart LR
    C[Frontend / Swagger UI] --> RL[Rate Limiter<br/>login + forgot-password]
    C --> F[JWT Filter<br/>+ Security Rules]
    RL --> CT
    F --> CT[Controller]
    CT --> S[Service<br/>Business Logic]
    S --> R[Repository / Specifications<br/>Spring Data JPA]
    R --> DB[(MySQL)]
    S --> S3[AWS S3<br/>profile images]
    S --> M[Gmail SMTP<br/>async emails]
```

---

## 🔑 Authentication Flow

```mermaid
sequenceDiagram
    participant U as Seller
    participant A as AuthController
    participant DB as MySQL

    U->>A: POST /auth/register
    A->>DB: Save seller (BCrypt) + default pricing for every garment type
    U->>A: POST /auth/login
    A->>A: Rate-limit check (email + IP)
    A->>DB: Verify credentials, save refresh token
    A-->>U: Access token (15 min) + Refresh token (30 days)
    U->>A: Request + Authorization: Bearer access token
    A-->>U: Protected data
    Note over U,A: Access token expired
    U->>A: POST /auth/refresh (refresh token)
    A->>DB: Validate refresh token and expiry
    A-->>U: New access token
    U->>A: POST /auth/logout
    A->>DB: Delete refresh token
```

### 🔁 Password reset

```mermaid
flowchart LR
    A[POST /auth/forgot-password] --> B{Email exists?}
    B -- Yes --> C[Create UUID token<br/>expires in 15 min]
    C --> D[Send reset link by email<br/>async]
    B -- No --> E[Do nothing]
    D --> F[Same generic response]
    E --> F
    F --> G[POST /auth/reset-password<br/>token + new password]
    G --> H{Token valid<br/>and not expired?}
    H -- Yes --> I[Password updated<br/>token deleted]
    H -- No --> J[Error]
```

The response is identical whether or not the email exists, so attackers cannot find out which emails are registered.

---

## 🧾 Order Flow

```mermaid
flowchart TD
    A[POST /order<br/>customer details + garments] --> B{Garments<br/>added?}
    B -- No --> X[Error: garments required]
    B -- Yes --> C[Fetch seller's price<br/>for each garment type]
    C --> D[Price x quantity per item<br/>sum = total bill]
    D --> E[Save order as RECEIVED<br/>linked to the seller]
    E --> F[Seller can edit order,<br/>search it or view on dashboard]
    F --> G[PUT /order/id/status = DELIVERED]
    G --> H[Order locked<br/>no edit or delete]
    G --> I[Delivery email sent to customer<br/>async]
```

```mermaid
stateDiagram-v2
    [*] --> RECEIVED: order created
    RECEIVED --> RECEIVED: edit / update details
    RECEIVED --> DELIVERED: mark delivered
    DELIVERED --> [*]: locked, no further changes
```

Every order operation checks that the order **belongs to the logged-in seller**.

---

## 🗄️ Database Design

```mermaid
erDiagram
    USER ||--o{ ORDER : owns
    USER ||--o{ PRICING : "sets prices"
    USER ||--o| REFRESH_TOKEN : has
    USER ||--o| PASSWORD_RESET_TOKEN : has
    ORDER ||--|{ GARMENT : contains

    USER {
        long id
        string username
        string email
        string businessName
        string profileImageUrl
        enum role
    }
    ORDER {
        int id
        string customerName
        string phone
        decimal totalAmount
        enum status
        datetime createdAt
    }
    GARMENT {
        enum type
        int quantity
        decimal pricePerItem
    }
    PRICING {
        enum garmentType
        decimal price
    }
```

**Garment types:** `SHIRT` `PANT` `JEANS` `COAT_PANT` `BLAZER` `SAREE` `LEHENGA`

---

## 🔌 API Endpoints

Base path: `/api/v1`. Everything except Auth and Health needs `Authorization: Bearer <access_token>` with the `SELLER` role.

### 🔓 Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a seller (default prices created) |
| POST | `/auth/login` | Public, rate-limited | Get access + refresh tokens |
| POST | `/auth/refresh` | Public | Get a new access token |
| POST | `/auth/logout` | Public | Revoke refresh token |
| POST | `/auth/forgot-password` | Public, rate-limited | Email a reset link |
| POST | `/auth/reset-password` | Public | Set a new password using the token |
| PUT | `/auth/change-password` | Seller | Change password (forces re-login) |

### 🧾 Orders
| Method | Endpoint | Description |
|---|---|---|
| POST | `/order` | Create order (bill auto-calculated) |
| GET | `/order?id=&status=&search=&days=&page=` | Filter, search and paginate orders |
| GET | `/order/recent?page=&size=` | Most recent orders |
| PATCH | `/order/{id}` | Edit customer details or garments |
| PUT | `/order/{id}/status?status=` | Update status (`RECEIVED` / `DELIVERED`) |
| DELETE | `/order/{orderId}` | Delete order (not allowed once delivered) |

### 💰 Pricing, 📊 Dashboard, 👤 Profile, ❤️ Health
| Method | Endpoint | Description |
|---|---|---|
| GET | `/pricing` | View my price list |
| PUT | `/pricing/{garmentType}?price=` | Update price of a garment type |
| GET | `/dashboard` | This month's orders, revenue, orders per status |
| GET / PATCH | `/profile` | View / update business profile |
| PUT | `/profile/image` | Upload profile photo (multipart `file`) to S3 |
| DELETE | `/profile/image` | Remove profile photo |
| GET | `/health` | Health check (public) |

---

## 🗂️ Project Structure

```
src/main/java/com/example/LaundryApplication
├── configuration/
│   ├── Security/      SecurityConfig, JwtAuthenticationFilter, S3Config, UserDetailsService
│   ├── controller/    AuthController
│   ├── service/       AuthService, JwtService, RateLimitService
│   ├── dao, dto, model/   refresh token + reset token layers
├── controller/        Order, Pricing, Dashboard, Profile, Health
├── service/           Order, Pricing, Dashboard, Profile, ImageStorage (S3)
├── dao/               JPA repositories
├── model/             User, OrderEntity, Garment, Pricing
├── dto/               Request / response objects
├── transformer/       Entity <-> DTO mapping
├── enums/             Role, OrderStatus, GarmentType
├── utility/           Email, Validation, OrderSpecification, CleanUpService
└── ecxeption/         Custom exceptions + global handler
```

---

## ⚙️ Getting Started

### Prerequisites
Java 21 · Maven · MySQL · Gmail account with an app password · AWS account with an S3 bucket

### 1. Clone
```bash
git clone https://github.com/dhruvkhurana1626/laundry-management-backend.git
cd laundry-management-backend
```

### 2. Create the database
```sql
CREATE DATABASE laundryapplication;
```
Tables are created automatically (`ddl-auto=update`).

### 3. Set environment variables

| Variable | Purpose |
|---|---|
| `DB_USERNAME`, `DB_PASSWORD` | MySQL credentials |
| `jwt_secret` | Secret used to sign JWTs |
| `GMAIL_ACCOUNT`, `GMAIL_PASSWORD` | Gmail address and app password for sending emails |
| AWS credentials | Picked up from the standard AWS credential chain (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, or `~/.aws`) |

Also update `aws.s3.bucket-name` and `aws.region` in `application-prod.properties` to your own bucket.

### 4. Run
```bash
./mvnw spring-boot:run
```
Server starts on **http://localhost:8080**

### 5. Open Swagger UI
👉 **http://localhost:8080/swagger-ui/index.html**

---

## 🧪 Quick Test Flow

1. `POST /auth/register`, then `POST /auth/login` and copy the access token.
2. Click **Authorize** in Swagger and paste the token.
3. `GET /pricing` to see the default prices, and `PUT /pricing/SHIRT?price=30` to change one.
4. `POST /order` with a customer and a few garments. The bill is calculated for you.
5. `PUT /order/{id}/status?status=DELIVERED` and check `GET /dashboard`.

---

## 🚀 Future Improvements
- More order stages (e.g. In Process, Ready for Pickup)
- Redis-backed rate limiting
- Dashboard queries done in the database instead of in memory
- Docker + docker-compose setup
- Unit and integration tests

---

## 👤 Author

**Dhruv Khurana**
[GitHub](https://github.com/dhruvkhurana1626)
