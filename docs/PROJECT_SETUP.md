# Bookstore POS System and Digital Library Website

## Recommended Project Structure

```text
bookstore-pos-digital-library/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/digilibfpj/pos/
│   │   │   ├── BookstorePosDigitalLibraryApplication.java
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthViewController.java
│   │   │   │   ├── CheckoutController.java
│   │   │   │   └── SupplierAdminController.java
│   │   │   ├── dto/
│   │   │   │   ├── CheckoutItemRequest.java
│   │   │   │   ├── CheckoutRequest.java
│   │   │   │   └── CheckoutResponse.java
│   │   │   ├── entity/
│   │   │   │   ├── Book.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Inventory.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── OrderLog.java
│   │   │   │   └── Supplier.java
│   │   │   ├── repository/
│   │   │   │   ├── BookRepository.java
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── InventoryRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   ├── OrderLogRepository.java
│   │   │   │   └── SupplierRepository.java
│   │   │   └── service/
│   │   │       ├── AdminDashboardService.java
│   │   │       ├── CheckoutService.java
│   │   │       └── HarvardLibraryService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/css/starry-bookshelf.css
│   │       └── templates/auth/
│   │           ├── admin-login.html
│   │           └── user-login.html
└── docs/
    └── PROJECT_SETUP.md
```

## MySQL Schema

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

## Cloud Environment Variables

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- PORT
- HARVARD_API_BASE_URL
- HARVARD_API_KEY
- RECEIPT_OUTPUT_DIR
- MEMBER_DISCOUNT_PERCENTAGE

## Architecture Notes

- Authentication can be mapped with Spring Security and role-based URL access for USER and ADMIN.
- Catalog search should first query `BookRepository`, then merge with Harvard Library API records.
- Checkout workflow must run in one transaction to ensure order creation and inventory deduction stay consistent.
- Supplier CRUD endpoints are under `/api/admin/suppliers`.
- Low-stock alert data source is `InventoryRepository.findLowStockItems()`.
