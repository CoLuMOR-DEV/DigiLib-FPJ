package com.bookstore.pos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class ReceiptService {
    private static final DecimalFormat MONEY = new DecimalFormat("0.00");

    public Path writeReceipt(Order order) throws IOException {
        Path receiptsDir = Path.of("receipts");
        if (!Files.exists(receiptsDir)) {
            Files.createDirectories(receiptsDir);
        }
        Path output = receiptsDir.resolve(order.getOrderId() + ".txt");

        StringBuilder sb = new StringBuilder();
        sb.append("=== DigiLib Bookstore Receipt ===\n");
        sb.append("Order ID: ").append(order.getOrderId()).append("\n");
        sb.append("Date: ").append(order.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Customer: ").append(order.getCustomer().getName()).append("\n");
        sb.append("Member: ").append(order.getCustomer().isMember() ? "Yes (10% discount)" : "No").append("\n\n");
        sb.append("Items:\n");

        for (CartItem item : order.getItems()) {
            sb.append("- ").append(item.getBook().getTitle())
                    .append(" x").append(item.getQuantity())
                    .append(" @ $").append(MONEY.format(item.getBook().getPrice()))
                    .append(" = $").append(MONEY.format(item.getLineTotal())).append("\n");
        }

        sb.append("\nSubtotal: $").append(MONEY.format(order.getSubtotal())).append("\n");
        sb.append("Discount: -$").append(MONEY.format(order.getDiscount())).append("\n");
        sb.append("TOTAL: $").append(MONEY.format(order.getTotal())).append("\n");

        Files.writeString(output, sb.toString());
        return output;
    }
}
