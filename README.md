# DigiLib Bookstore POS (Java Swing)

A functioning Bookstore POS desktop application with a modern Apple-inspired GUI theme.

## Features Implemented
- Apple-style UI with light/dark mode toggle, clean cards, and iOS-inspired accent styling
- Login (`admin` / `book123`)
- Search books by title or author
- Add to cart and remove from cart
- Stock tracking (updates in real time)
- Membership discount (10%)
- Supplier management (assign supplier to selected book)
- Low-stock alert dashboard
- Receipt printing as `.txt` files in `receipts/`

## Run
```bash
javac -d out src/main/java/com/bookstore/pos/*.java
java -cp out com.bookstore.pos.BookstorePOSApp
```

## OOP Classes
- `Book`
- `Inventory`
- `Customer`
- `Order`
- `Supplier`
- `CartItem`
- `ReceiptService`
- `BookstorePOSApp`
