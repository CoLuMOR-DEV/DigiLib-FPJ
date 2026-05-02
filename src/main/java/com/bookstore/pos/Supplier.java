package com.bookstore.pos;

public class Supplier {
    private final String id;
    private final String name;
    private final String contact;

    public Supplier(String id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return name;
    }
}
