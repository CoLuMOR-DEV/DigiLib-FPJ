# Bookstore POS System and Digital Library Website

## Technology Stack

- Backend: Java Spring Boot (Spring Web, Spring Data JPA, Spring Security, Thymeleaf)
- Database: MySQL on localhost via XAMPP and phpMyAdmin
- Frontend: HTML, CSS, JavaScript with Thymeleaf
- External API: Harvard Library API via `RestTemplate`

## Recommended Project Structure

```text
DigiLib-FPJ/
├── README.md
├── pom.xml
├── docs/
│   └── PROJECT_SETUP.md
└── src/main/
    ├── java/com/digilibfpj/pos/
    │   ├── BookstorePosDigitalLibraryApplication.java
    │   ├── config/
    │   │   └── AppConfig.java
    │   ├── controller/
    │   │   ├── AuthViewController.java
    │   │   ├── CheckoutController.java
    │   │   └── SupplierAdminController.java
    │   ├── dto/
    │   │   ├── CheckoutItemRequest.java
    │   │   ├── CheckoutRequest.java
    │   │   └── CheckoutResponse.java
    │   ├── entity/
    │   │   ├── Book.java
    │   │   ├── Customer.java
    │   │   ├── Inventory.java
    │   │   ├── OrderItem.java
    │   │   ├── OrderLog.java
    │   │   └── Supplier.java
    │   ├── repository/
    │   │   ├── BookRepository.java
    │   │   ├── CustomerRepository.java
    │   │   ├── InventoryRepository.java
    │   │   ├── OrderItemRepository.java
    │   │   ├── OrderLogRepository.java
    │   │   └── SupplierRepository.java
    │   └── service/
    │       ├── AdminDashboardService.java
    │       ├── CheckoutService.java
    │       └── HarvardLibraryService.java
    └── resources/
        ├── application.properties
        ├── static/
        │   ├── css/modern-ui.css
        │   └── js/theme-toggle.js
        └── templates/
            ├── admin-dashboard.html
            ├── home.html
            └── auth/
                ├── admin-login.html
                └── user-login.html
```

## Localhost Setup with XAMPP

### 1. Install Required Tools

- Java 17+
- Maven 3.9+
- XAMPP with MySQL enabled
- Browser

### 2. Create Database via phpMyAdmin

1. Start **Apache** and **MySQL** from XAMPP Control Panel.
2. Open `http://localhost/phpmyadmin`.
3. Create a database named `digilib_fpj`.

### 3. Default Local Database Configuration

`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/digilib_fpj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

This setup is already configured in the project.

### 4. Run the Application

```bash
mvn spring-boot:run
```

### 5. Open Pages

- Home: `http://localhost:8080/`
- User Login: `http://localhost:8080/login/user`
- Admin Login: `http://localhost:8080/login/admin`
- Admin Dashboard: `http://localhost:8080/admin/dashboard`

## MySQL Schema Reference

```sql
CREATE TABLE customer (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_member BOOLEAN NOT NULL
);

CREATE TABLE supplier (
    supplier_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(180) NOT NULL,
    contact_info VARCHAR(255)
);

CREATE TABLE book (
    book_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(180) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    supplier_id BIGINT NOT NULL,
    CONSTRAINT fk_book_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

CREATE TABLE inventory (
    inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL UNIQUE,
    stock_qty INT NOT NULL,
    low_alert_qty INT NOT NULL,
    CONSTRAINT fk_inventory_book FOREIGN KEY (book_id) REFERENCES book(book_id)
);

CREATE TABLE order_log (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_orderlog_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE order_item (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_orderitem_orderlog FOREIGN KEY (order_id) REFERENCES order_log(order_id),
    CONSTRAINT fk_orderitem_book FOREIGN KEY (book_id) REFERENCES book(book_id)
);
```

## Interacting with Key Features

### Supplier CRUD

```bash
curl -X POST http://localhost:8080/api/admin/suppliers \
  -H "Content-Type: application/json" \
  -d '{"name":"Supplier A","contactInfo":"supplier-a@demo.com"}'

curl http://localhost:8080/api/admin/suppliers
```

### Checkout and Receipt

```bash
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {"bookId": 1, "quantity": 2}
    ]
  }'
```

Successful checkout will:

- deduct stock in `inventory`
- write order data to `order_log` and `order_item`
- generate a receipt file under `receipts/`

### Harvard Library Search Integration Flow

- search locally by title/author with `BookRepository`
- call Harvard Library API for additional bibliographic data
- merge local and external results in `HarvardLibraryService`

## UI/UX Theme Notes

The UI uses a modern minimalist design with:

- responsive cards and clean spacing
- sleek sans-serif typography
- subtle hover transitions and elevated surfaces
- Dark/Light mode toggle in the navbar on all major pages
- persisted theme preference using `localStorage`

## Dark/Light Mode Implementation

- CSS variables for both themes: `src/main/resources/static/css/modern-ui.css`
- JavaScript toggle and persistence: `src/main/resources/static/js/theme-toggle.js`
- toggle button exists in user login, admin login, home, and admin dashboard templates


## Maven Error Fix for Corrupted Local Cache (Windows)

If you see errors like:

- `Non-parseable POM ... maven-filtering-3.3.1.pom: ... \u0000`
- `No implementation for MavenResourcesFiltering was bound`

your local Maven cache has corrupted plugin POM files.

### Quick Fix (PowerShell)

Run this script from project root:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\fix-maven-cache.ps1
```

This removes corrupted cache folders and forces Maven to download clean copies.

### Manual Fix (PowerShell)

```powershell
Remove-Item "$env:USERPROFILE\.m2\repository\org\apache\maven\shared\maven-filtering\3.3.1" -Recurse -Force
Remove-Item "$env:USERPROFILE\.m2\repository\commons-io\commons-io\2.11.0" -Recurse -Force
Remove-Item "$env:USERPROFILE\.m2\repository\org\apache\maven\plugins\maven-resources-plugin\3.3.1" -Recurse -Force
mvn -U clean
mvn -U spring-boot:run
```

Use `spring-boot:run` and not `sprint-boot:run`.

## One-Command Local Run Script (Windows)

You can run local defaults with:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run-local.ps1
```


## VS Code Diagnostics Clarification

If VS Code shows `The build file has been changed and may need reload to make it effective`, this is an IDE refresh notice.

Use one of these actions:

- Command Palette -> `Java: Clean Java Language Server Workspace`
- Command Palette -> `Maven: Reload project`
- Restart VS Code window

The Red Hat Dependency Analytics message for `mysql-connector-j` in this project is informational in this setup and does not block build/run.


## Admin Credentials

Default admin account is auto-created on startup:

- Username: `admin`
- Password: `admin123`

These values come from `application.properties`:

```properties
admin.default.username=admin
admin.default.password=admin123
```

To change admin credentials:

1. Edit those two values in `src/main/resources/application.properties`.
2. If the old admin user already exists in database, update its password in `customer` table using BCrypt hash or delete that row and restart the app so it recreates with new values.

## Access Model

- Public users can open Home and browse the catalog without signing in.
- New users can create an account at `/signup`.
- User login page: `/login/user`.
- Admin login page: `/login/admin`.
- Admin dashboard requires ADMIN role.
