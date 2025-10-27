# Crave Restaurant

Object Oriented Analysis and Design group project — SE2012.

Customers browse the menu, keep a cart, place cash-on-delivery orders, and track their status. Admins manage dishes and update orders.

## Features

- Register and login using JWT; USER and ADMIN roles.
- Public menu with categories, search, availability, and six sample dishes.
- Cart saved in localStorage, with quantity updates and removal.
- Separate Cart and Checkout pages with delivery details and an optional note.
- Cash on delivery only; no payment gateway.
- My Orders page with a status display and refresh button.
- Admin dashboard with dishes and orders tabs.
- Create, edit, and delete dishes; update order status one step at a time.
- Validation, readable error messages, and role-based API protection.
- Order prices are calculated on the backend. Dish names and prices are saved with each order so later edits or deletion do not change past orders.

## Tech stack

- Backend: Java 17, Spring Boot 3, Spring Web, Data JPA, Security, JWT, Lombok, MySQL, and optional H2.
- Frontend: React, Vite, React Router, Axios, and plain CSS.
- JWT signing and verification use Spring Security's built-in Nimbus support via the OAuth2 resource server dependency. No external identity provider is needed.

## Project structure

```text
backend/
  pom.xml
  src/main/java/com/crave/
    config/       # Security, JWT, and startup data
    controller/   # REST endpoints and exception handler
    dto/          # Request validation and response objects
    model/        # JPA entities and role/status enums
    repository/   # Database access
    service/      # Application logic
  src/main/resources/
  src/test/java/com/crave/
frontend/
  src/
    components/   # Navbar, route protection, dish/order components
    context/      # AuthContext and CartContext
    pages/        # One file per main page
    api.js        # Axios and display helpers
    styles.css    # Global plain CSS
```

## Prerequisites

- JDK 17 or newer (the backend compiles for Java 17).
- Maven 3.6.3 or newer.
- Node.js 22.12 or newer and npm.
- MySQL 8 if using the default database; H2 requires no database installation.

## Run the backend with MySQL

1. Start MySQL locally.
2. Update `backend/src/main/resources/application.properties` with your database username and password, or set the environment variables below. The default URL creates the `crave_restaurant` database if your account has permission.
3. From the repository root:

```sh
cd backend
mvn spring-boot:run
```

Backend: **http://localhost:8080**. The API starts at `/api`; the backend root is not a webpage.

The database defaults to `root` with an empty password. In PowerShell, you can configure your credentials without changing the file:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-mysql-password"
# Optional: override the full JDBC URL as well
$env:DB_URL = "jdbc:mysql://localhost:3306/crave_restaurant?createDatabaseIfNotExist=true"
cd backend
mvn spring-boot:run
```

If your MySQL account cannot create databases, run `CREATE DATABASE crave_restaurant;` using an account that can, then start the app. Hibernate creates the tables.

## Easy backend fallback: H2

From the repository root, run:

```sh
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

This uses an embedded H2 database stored in `backend/data/` and keeps data between runs. No MySQL server is needed. Alternatively, use the commented H2 datasource settings in `application.properties`. Keep only one backend running on port 8080.

## Run the frontend

Open a **second terminal** in the repository root:

```sh
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. Keep the backend running while using the frontend. Use `localhost` to match the configured CORS origin.

Axios defaults to `http://localhost:8080/api`. To change it, create `frontend/.env` containing `VITE_API_URL=http://localhost:8080/api` and restart Vite. The frontend uses port 5173 and reports an error if it is occupied.

## Default admin

- Email: **admin@crave.com**
- Password: **admin123**

The admin and sample dishes are seeded at startup. The admin is added only if its email does not exist; sample dishes are added only when the dish table is empty. Register a separate customer account to place orders. All registered accounts receive the USER role.

The JWT lasts 24 hours. Logout removes it from the browser; an expired token requires signing in again. The provided JWT secret and admin password are for this local learning project. Set `JWT_SECRET` to a private value of at least 32 bytes before sharing a deployment.

## Try the complete flow

1. Register a customer, browse the menu, and add a dish.
2. Open Cart, change its quantity, and continue to Checkout.
3. Enter your name, phone, address, and optional note, then place the order.
4. Checkout displays: **Payment method: Cash on Delivery - please pay the rider when your order arrives.**
5. Open a separate browser profile and log in as admin. On the orders tab, advance the order through each status.
6. Refresh My Orders in the customer browser to see the latest status.

```text
PENDING -> PREPARING -> DELIVERING -> DELIVERED
```

Orders start as PENDING. Admins can only select the next status. Prices display in Sri Lankan rupees (Rs.); delivery is free in this project. No card information is collected. Order status updates are loaded using the refresh buttons.

## API endpoints

All request and response bodies use JSON. Protected requests need `Authorization: Bearer <token>`.

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | Public | Register a customer and return JWT + user |
| POST | `/api/auth/login` | Public | Login and return JWT + user |
| GET | `/api/dishes` | Public | List dishes |
| POST | `/api/dishes` | ADMIN | Create a dish |
| PUT | `/api/dishes/{id}` | ADMIN | Edit a dish |
| DELETE | `/api/dishes/{id}` | ADMIN | Delete a dish |
| POST | `/api/orders` | USER | Place an order |
| GET | `/api/orders/my` | USER | Get the current customer's orders |
| GET | `/api/orders` | ADMIN | Get all orders |
| PUT | `/api/orders/{id}/status` | ADMIN | Advance order status |

Register body:

```json
{ "name": "Student", "email": "student@example.com", "password": "student123" }
```

Login body:

```json
{ "email": "student@example.com", "password": "student123" }
```

Dish body (POST or PUT):

```json
{
  "name": "Chicken Burger",
  "description": "Grilled chicken with lettuce and house sauce",
  "price": 1250.00,
  "imageUrl": "https://example.com/burger.jpg",
  "category": "Burgers",
  "available": true
}
```

Order body (use a dish ID returned by GET `/api/dishes`):

```json
{
  "fullName": "Student",
  "phone": "0771234567",
  "deliveryAddress": "10 Main Street, Colombo",
  "note": "Please ring the bell",
  "items": [{ "dishId": 1, "quantity": 2 }]
}
```

Status update body:

```json
{ "status": "PREPARING" }
```

Successful creates return 201, deletion returns 204, and other successful endpoints return 200. Validation failures return 400, missing/invalid tokens return 401, denied roles return 403, missing records return 404, and duplicate emails or conflicting updates return 409. Application errors return a `message` field; authentication errors use Spring Security's standard responses.

## Checks and build

```sh
cd backend
mvn test
mvn package
```

The integration tests use an isolated in-memory H2 database. They cover authentication, validation, role restrictions, CORS, dish updates, order ownership, totals, unavailable dishes, status flow, and saved dish details after deletion.

```sh
cd frontend
npm run build
```

The frontend output is in `frontend/dist/`. Dish images and fonts use external URLs; the menu provides a fallback when a dish image cannot load. Java 21 can run the backend too, but the source/bytecode target remains Java 17.

Verified during development: all five backend integration tests pass, the frontend production build succeeds, and a Chrome check covers menu loading, cart persistence, checkout protection, registration, order placement, admin login, status updates, and mobile layout. The live backend was checked with H2; MySQL requires your local credentials. If port 8080 is already occupied, stop that application before starting this backend, or run on another port and update `VITE_API_URL` to match.

JWT reference: [Spring Security JWT documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html).
