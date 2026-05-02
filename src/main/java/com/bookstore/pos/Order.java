package com.bookstore.pos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    public static final double MEMBER_DISCOUNT_RATE = 0.10;

    private final String orderId;
    private final LocalDateTime timestamp;
    private final Customer customer;
    private final List<CartItem> items;

    public Order(String orderId, Customer customer, List<CartItem> items) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.timestamp = LocalDateTime.now();
    }

    public String getOrderId() { return orderId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Customer getCustomer() { return customer; }
    public List<CartItem> getItems() { return new ArrayList<>(items); }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    public double getDiscount() {
        return customer.isMember() ? getSubtotal() * MEMBER_DISCOUNT_RATE : 0;
    }

    public double getTotal() {
        return getSubtotal() - getDiscount();
    }
}
