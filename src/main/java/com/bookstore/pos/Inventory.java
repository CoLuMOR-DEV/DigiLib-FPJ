package com.bookstore.pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Inventory {
    private final List<Book> books = new ArrayList<>();
    private final int lowStockThreshold;

    public Inventory(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Book> searchByTitleOrAuthor(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase(Locale.ROOT).contains(q)
                        || b.getAuthor().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    public List<Book> getLowStockBooks() {
        return books.stream().filter(b -> b.getStock() <= lowStockThreshold).collect(Collectors.toList());
    }
}
