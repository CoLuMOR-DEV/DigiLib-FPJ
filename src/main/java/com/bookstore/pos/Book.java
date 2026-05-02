package com.bookstore.pos;

public class Book {
    private final String id;
    private final String title;
    private final String author;
    private final double price;
    private int stock;
    private Supplier supplier;

    public Book(String id, String title, String author, double price, int stock, Supplier supplier) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.supplier = supplier;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public Supplier getSupplier() { return supplier; }

    public void setStock(int stock) { this.stock = stock; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}
