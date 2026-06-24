# E-commerce Backend with Spring Data JPA

A comprehensive e-commerce backend system built with Spring Boot 3, Spring Data JPA, and PostgreSQL.

## Features

- ✅ Complete e-commerce database schema with relationships
- ✅ Product catalog with categories, inventory, and pricing
- ✅ Order management with transactional processing
- ✅ User authentication with JWT
- ✅ Payment processing simulation
- ✅ Database migrations with Flyway
- ✅ Query optimization with indexes and caching
- ✅ Connection pooling with HikariCP
- ✅ Comprehensive REST API with Swagger UI

## Technology Stack

| Technology       | Version  |
|-----------------|---------|
| Java             | 17      |
| Spring Boot      | 3.2.0   |
| Spring Data JPA  | Latest  |
| PostgreSQL       | 15      |
| Flyway           | Latest  |
| HikariCP         | Built-in|
| JWT (jjwt)       | 0.11.5  |
| Lombok           | 1.18.30 |
| SpringDoc OpenAPI| 2.2.0   |

## Database Schema

```sql
users          (id, email, password, first_name, last_name, phone, role, is_active, created_at, updated_at)
categories     (id, name, description, parent_category_id, created_at, updated_at)
products       (id, name, description, price, stock, category_id, image_url, is_active, created_at, updated_at)
orders         (id, order_number, user_id, total_amount, status, shipping_address, created_at, updated_at)
order_items    (id, order_id, product_id, quantity, unit_price, subtotal)
payments       (id, order_id, amount, payment_method, status, transaction_id, created_at, updated_at)
```

## Entity Relationships

- One **User** has Many **Orders**
- One **Order** has Many **OrderItems**
- One **Order** has One **Payment**
- One **Product** belongs to One **Category**
- One **Category** has Many **Products**
- One **Category** can have a Parent **Category** (hierarchical)

## Quick Start

### Using Docker Compose

```bash
docker-compose up -d
```
Application will be at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`

### Manual Setup

```bash
# 1. Start PostgreSQL (or use Docker)
docker run -d --name pg -e POSTGRES_DB=ecommerce_db \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password \
  -p 5432:5432 postgres:15-alpine

# 2. Build and run
mvn clean package -DskipTests
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Authentication
| Method | Endpoint              | Description        |
|--------|-----------------------|--------------------|
| POST   | /api/auth/register    | Register user      |
| POST   | /api/auth/login       | Login              |

### Products (Public GET, Admin POST/PUT/DELETE)
| Method | Endpoint                        | Description             |
|--------|---------------------------------|-------------------------|
| GET    | /api/products                   | List all (paginated)    |
| GET    | /api/products/{id}              | Get by ID               |
| GET    | /api/products/search?keyword=   | Search products         |
| GET    | /api/products/category/{id}     | By category             |
| GET    | /api/products/price-range       | Filter by price         |
| POST   | /api/products                   | Create (Admin)          |
| PUT    | /api/products/{id}              | Update (Admin)          |
| DELETE | /api/products/{id}              | Delete (Admin)          |

### Orders (Auth required)
| Method | Endpoint                  | Description           |
|--------|---------------------------|-----------------------|
| GET    | /api/orders               | My orders             |
| POST   | /api/orders               | Create order          |
| GET    | /api/orders/{id}          | Order details         |
| PUT    | /api/orders/{id}/cancel   | Cancel order          |
| GET    | /api/orders/report/daily  | Daily report (Admin)  |

### Payments (Auth required)
| Method | Endpoint                     | Description           |
|--------|------------------------------|-----------------------|
| POST   | /api/payments                | Process payment       |
| GET    | /api/payments/order/{orderId}| Get payment by order  |

## Sample API Usage

### 1. Register & Login
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"pass123","firstName":"John","lastName":"Doe"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"pass123"}'
```

### 2. Create Order
```bash
TOKEN="your_jwt_token_here"
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"productId": 1, "quantity": 2}],
    "shippingAddress": "123 Main St, City"
  }'
```

### 3. Process Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"orderId": 1, "paymentMethod": "CREDIT_CARD"}'
```

## Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_db
    username: postgres
    password: password
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

## Flyway Migrations

| Version | File                    | Description              |
|---------|-------------------------|--------------------------|
| V1      | V1__initial_schema.sql  | Create all tables        |
| V2      | V2__seed_data.sql       | Insert sample data       |
| V3      | V3__add_indexes.sql     | Add performance indexes  |

## Default Users (from seed data)

| Email                     | Password     | Role     |
|---------------------------|--------------|----------|
| admin@ecommerce.com       | admin123     | ADMIN    |
| john.doe@example.com      | password123  | CUSTOMER |
| jane.smith@example.com    | password123  | CUSTOMER |

## Performance Features

- **HikariCP** connection pooling (10 max connections)
- **Database indexes** on frequently queried columns
- **Spring Cache** for product catalog (ConcurrentMapCacheManager)
- **Lazy loading** on entity relationships to avoid N+1 queries
- **Batch inserts** configured (batch_size: 20)
- **Read-only transactions** for query methods

## Running Tests

```bash
mvn test
```
