# Project Setup Blueprint

## Architecture

- Presentation Layer: Thymeleaf templates + CSS + JavaScript
- Business Layer: Spring `@Service` components
- Data Layer: Spring Data JPA repositories with MySQL

## Local XAMPP Configuration

Use XAMPP MySQL with:

- Host: `localhost`
- Port: `3306`
- Database: `digilib_fpj`
- Username: `root`
- Password: empty

`application.properties` is already configured for this setup.

## JPA Entity Focus: OrderLog and OrderItem

`OrderLog`

- `order_id` primary key
- `customer_id` foreign key to `customer`
- `order_date` timestamp
- `total_amount` decimal
- one-to-many relationship with `OrderItem`

`OrderItem`

- `item_id` primary key
- `order_id` foreign key to `order_log`
- `book_id` foreign key to `book`
- `quantity` integer

## Service Responsibilities

`CheckoutService`

- validates customer and cart items
- checks and deducts stock quantity
- applies percentage member discount
- writes `order_log` and `order_item` records
- generates `.txt` receipt file in local `receipts/` directory

`HarvardLibraryService`

- local-first search by title and author
- external Harvard API fetch via `RestTemplate`
- response payload containing both local and external data

## UI Theme Implementation

- minimalist responsive layout
- dark/light mode with CSS variables
- navbar toggle available on main pages
- `localStorage` persistence for theme state
