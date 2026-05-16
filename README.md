# Laundry Management System — Backend

## Overview

Spring Boot based REST API for managing laundry orders, order tracking, status updates, and email notifications.

---

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL
* Railway MySQL
* AWS EC2
* Maven

---

## Features

* Create laundry orders
* Update order status
* Delete orders
* Search & filter orders
* Pagination support
* Email notifications for READY & DELIVERED orders
* Basic Authentication using Spring Security
* CORS configuration support

---

## API Endpoints

```http
POST   /api/v1/order
GET    /api/v1/order/recent
GET    /api/v1/order
PUT    /api/v1/order/{id}/status
DELETE /api/v1/order/{id}
```

---

## Environment Variables

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=

GMAIL_ACCOUNT=
GMAIL_PASSWORD=
```

---

## Deployment

* Backend: AWS EC2
* Database: Railway MySQL
* Frontend: Vercel

---

## Future Improvements

* JWT Authentication
* Multi-user seller accounts
* PDF invoice generation
* Docker support

---

## Author

Dhruv Khurana
