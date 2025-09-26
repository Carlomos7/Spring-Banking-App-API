# Spring Banking API

This project is a backend banking application built with Spring Boot. It exposes a RESTful API for customer management, account operations, and transaction processing. The system applies double-entry bookkeeping principles using journals and ledger entries to ensure that transactions are always balanced and auditable.

---

## Technology Stack

* **Language**: Java 17
* **Framework**: Spring Boot 3.4.10

  * `spring-boot-starter-web` for REST APIs
  * `spring-boot-starter-data-jpa` for persistence
  * `spring-boot-starter-security` for authentication and authorization
  * `spring-boot-starter-validation` for request validation
* **Database**: H2 (in-memory, development)
* **Documentation**: SpringDoc OpenAPI (Swagger UI)
* **Build Tool**: Maven with `spring-boot-maven-plugin`

---

## How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/Carlomos7/Spring-Banking-App-API
cd banking-api
```

### 2. Run with Maven

```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

### 3. Access the Application

* Base URL: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* H2 Console (development profile): `http://localhost:8080/h2-console`

---

## Features

### Customers

* Register new users with unique username and email
* Authenticate via login (username or email + password)
* Retrieve and update customer profiles

### Accounts

* Open new accounts (checking, savings, internal)
* Toggle account status (active/inactive)
* Retrieve account details by ID
* List all accounts for a customer

### Journals and Ledger Entries

* Create journals for grouping transactions
* Add debit and credit ledger entries to journals
* Enforce balanced double-entry bookkeeping
* Post journals to finalize transactions
* Retrieve ledger history for specific accounts (with pagination)

### Security

* Passwords hashed using BCrypt
* Input validation on registration (strong password policy enforced)
* HTTP Basic Authentication enabled for development profile

---

## API Endpoints

### Root

* **GET /** – Returns API information

### Authentication

* **POST /auth/register** – Register a new customer
* **POST /auth/login** – Authenticate a customer

### Customers

* **GET /customers/{id}** – Get customer details by ID
* **PATCH /customers/{id}** – Update customer profile

### Accounts

* **POST /customers/{customerId}/accounts** – Open a new account
* **GET /customers/{customerId}/accounts** – List all accounts for a customer
* **GET /accounts/{accountId}?customerId={customerId}** – Get account details
* **PATCH /accounts/{accountId}/active** – Activate or deactivate an account

### Journals

* **POST /journals?description={desc}&externalRef={ref}** – Create a new journal
* **GET /journals/{journalId}** – Retrieve a journal by ID
* **POST /journals/{journalId}/entries** – Add a ledger entry to a journal
* **GET /journals/{journalId}/entries** – List entries for a journal
* **POST /journals/{journalId}/post** – Post (finalize) a journal

### Ledger Entries

* **GET /accounts/{accountId}/entries?page={n}&size={m}** – Get ledger history for an account

---

## Database Schema (Simplified)

* **Customer** – Username, email, password hash, profile fields
* **Account** – Linked to a customer, currency, account type, active flag, timestamps
* **Journal** – Transaction container with description, external reference, status (PENDING/POSTED)
* **LedgerEntry** – Debit/credit posting tied to both a journal and an account

---

## Sample Requests

### Register a Customer

```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
  "username": "alice",
  "firstName": "Alice",
  "lastName": "Smith",
  "email": "alice@email.com",
  "password": "StrongPass123!"
}'
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{
  "identifier": "alice",
  "password": "StrongPass123!"
}'
```

### Open an Account

```bash
curl -X POST http://localhost:8080/customers/{customerId}/accounts \
-H "Content-Type: application/json" \
-d '{
  "kind": "checking",
  "currency": "USD"
}'
```

### Create a Journal and Add Entries

```bash
# Create a journal
curl -X POST "http://localhost:8080/journals?description=Transfer&externalRef=12345"

# Add a debit entry
curl -X POST http://localhost:8080/journals/{journalId}/entries \
-H "Content-Type: application/json" \
-d '{
  "accountId": "{accountId}",
  "side": "DEBIT",
  "amountCents": 10000,
  "currency": "USD"
}'

# Add a credit entry
curl -X POST http://localhost:8080/journals/{journalId}/entries \
-H "Content-Type: application/json" \
-d '{
  "accountId": "{accountId}",
  "side": "CREDIT",
  "amountCents": 10000,
  "currency": "USD"
}'

# Post the journal
curl -X POST http://localhost:8080/journals/{journalId}/post
```

---

## Why I Built This

This project allowed me to practice:

* Building REST APIs with Spring Boot
* Modeling banking operations with double-entry bookkeeping
* Applying strong validation and security practices in Spring Security
* Using JPA/Hibernate for entity persistence and relationships
* Documenting APIs with OpenAPI/Swagger for easy exploration
