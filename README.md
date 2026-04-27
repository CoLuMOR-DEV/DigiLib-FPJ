# Bookstore POS System and Digital Library Website

This project is a Spring Boot starter for a Bookstore POS and Digital Library platform with:

- Spring Web + Thymeleaf
- Spring Data JPA + MySQL
- Role-ready authentication foundation
- Harvard Library API integration service
- Checkout workflow with member discounts, inventory deduction, and `.txt` receipt output
- Admin Supplier CRUD and low-stock alert query support

## 1. Prerequisites

Install the following on your local machine:

- Java 17+
- Maven 3.9+
- MySQL 8+
- Git

Check installed versions:

```bash
java -version
mvn -version
mysql --version
```

## 2. Clone and Enter the Project

```bash
git clone <your-repository-url>
cd DigiLib-FPJ
```

## 3. Create the Local MySQL Database

Open MySQL and run:

```sql
CREATE DATABASE digilib_fpj;
```

You can keep using the database with Hibernate auto-update (`spring.jpa.hibernate.ddl-auto=update`) or run the full explicit schema from `docs/PROJECT_SETUP.md`.

## 4. Configure Environment Variables for Localhost

This application reads DB/API settings from environment variables.

### macOS/Linux (bash/zsh)

```bash
export DB_URL="jdbc:mysql://localhost:3306/digilib_fpj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="your_mysql_password"
export PORT="8080"
export HARVARD_API_BASE_URL="https://api.lib.harvard.edu/v2/items.json"
export HARVARD_API_KEY=""
export RECEIPT_OUTPUT_DIR="receipts"
export MEMBER_DISCOUNT_PERCENTAGE="10"
```

### Windows PowerShell

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/digilib_fpj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:PORT="8080"
$env:HARVARD_API_BASE_URL="https://api.lib.harvard.edu/v2/items.json"
$env:HARVARD_API_KEY=""
$env:RECEIPT_OUTPUT_DIR="receipts"
$env:MEMBER_DISCOUNT_PERCENTAGE="10"
```

## 5. Start the Application

From the project root:

```bash
mvn spring-boot:run
```

If startup is successful, open:

- User login page: `http://localhost:8080/login/user`
- Admin login page: `http://localhost:8080/login/admin`

## 6. Interact with the App on Localhost

Because this is starter code, the easiest way to interact first is through REST endpoints plus the login pages.

### 6.1 Supplier Admin CRUD (REST)

Base URL:

```text
http://localhost:8080/api/admin/suppliers
```

Create supplier:

```bash
curl -X POST http://localhost:8080/api/admin/suppliers \
  -H "Content-Type: application/json" \
  -d '{"name":"Penguin Distribution","contactInfo":"penguin@example.com"}'
```

List suppliers:

```bash
curl http://localhost:8080/api/admin/suppliers
```

Update supplier:

```bash
curl -X PUT http://localhost:8080/api/admin/suppliers/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Penguin Distribution Intl","contactInfo":"support@penguin.example"}'
```

Delete supplier:

```bash
curl -X DELETE http://localhost:8080/api/admin/suppliers/1
```

### 6.2 Checkout Endpoint (REST)

Checkout URL:

```text
http://localhost:8080/api/checkout
```

Sample request:

```bash
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {"bookId": 1, "quantity": 2},
      {"bookId": 2, "quantity": 1}
    ]
  }'
```

On successful checkout:

- `order_log` and `order_item` records are inserted
- `inventory.stock_qty` is deducted
- a receipt text file is written to `RECEIPT_OUTPUT_DIR` (default: `receipts/`)

## 7. Optional Local Seed Data

Use this as a quick bootstrap in MySQL to test checkout:

```sql
INSERT INTO customer (username, password, role, is_member)
VALUES ('reader1', 'plain-password-change-me', 'USER', true);

INSERT INTO supplier (name, contact_info)
VALUES ('Vintage House Supply', 'vintage-house@example.com');

INSERT INTO book (isbn, title, author, price, supplier_id)
VALUES ('9780140449136', 'The Odyssey', 'Homer', 19.99, 1);

INSERT INTO inventory (book_id, stock_qty, low_alert_qty)
VALUES (1, 25, 5);
```

## 8. Troubleshooting

### Application fails on DB connection

- Verify MySQL is running
- Verify `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
- Verify database `digilib_fpj` exists

### Port already in use

Set another port before starting:

```bash
export PORT=8081
mvn spring-boot:run
```

### No receipt file generated

- Verify checkout completed successfully
- Verify process has write permissions to `RECEIPT_OUTPUT_DIR`

## 9. Notes for Next Steps

Recommended additions for a production-ready localhost experience:

- Add Spring Security config with encoded passwords and role-based route protection
- Add Flyway/Liquibase migrations
- Add a Home/Landing page and Admin Dashboard Thymeleaf views
- Add unit and integration tests for checkout, inventory deduction, and supplier CRUD
