package com.digilibfpj.pos.service;

import com.digilibfpj.pos.dto.CheckoutItemRequest;
import com.digilibfpj.pos.dto.CheckoutRequest;
import com.digilibfpj.pos.dto.CheckoutResponse;
import com.digilibfpj.pos.entity.Book;
import com.digilibfpj.pos.entity.Customer;
import com.digilibfpj.pos.entity.Inventory;
import com.digilibfpj.pos.entity.OrderItem;
import com.digilibfpj.pos.entity.OrderLog;
import com.digilibfpj.pos.repository.BookRepository;
import com.digilibfpj.pos.repository.CustomerRepository;
import com.digilibfpj.pos.repository.InventoryRepository;
import com.digilibfpj.pos.repository.OrderLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderLogRepository orderLogRepository;

    @Value("${member.discount-percentage:10}")
    private BigDecimal memberDiscountPercentage;

    @Value("${receipt.output-directory:receipts}")
    private String receiptOutputDirectory;

    public CheckoutService(CustomerRepository customerRepository,
                           BookRepository bookRepository,
                           InventoryRepository inventoryRepository,
                           OrderLogRepository orderLogRepository) {
        this.customerRepository = customerRepository;
        this.bookRepository = bookRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderLogRepository = orderLogRepository;
    }

    @Transactional
    public CheckoutResponse processCheckout(CheckoutRequest checkoutRequest) {
        Customer customer = customerRepository.findById(checkoutRequest.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + checkoutRequest.getCustomerId()));

        if (checkoutRequest.getItems() == null || checkoutRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Checkout cart is empty");
        }

        OrderLog orderLog = new OrderLog();
        orderLog.setCustomer(customer);
        orderLog.setOrderDate(LocalDateTime.now());

        List<String> receiptLines = new ArrayList<>();
        receiptLines.add("DigiLib FPJ Bookstore POS Receipt");
        receiptLines.add("Order Date: " + orderLog.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        receiptLines.add("Customer: " + customer.getUsername());
        receiptLines.add("----------------------------------------");

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CheckoutItemRequest itemRequest : checkoutRequest.getItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found: " + itemRequest.getBookId()));

            Inventory inventory = inventoryRepository.findByBook_BookId(itemRequest.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found for book: " + itemRequest.getBookId()));

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero for book: " + itemRequest.getBookId());
            }

            if (inventory.getStockQty() < itemRequest.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for book: " + book.getTitle());
            }

            BigDecimal lineAmount = book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(lineAmount);

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderLog.addOrderItem(orderItem);

            Integer updatedQty = inventory.getStockQty() - itemRequest.getQuantity();
            inventory.setStockQty(updatedQty);
            inventoryRepository.save(inventory);

            receiptLines.add(book.getTitle() + " | Qty: " + itemRequest.getQuantity() + " | Unit: " + book.getPrice() + " | Line: " + lineAmount);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(customer.getIsMember())) {
            discountAmount = subtotal
                    .multiply(memberDiscountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal finalTotal = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        orderLog.setTotalAmount(finalTotal);

        OrderLog savedOrder = orderLogRepository.save(orderLog);

        receiptLines.add("----------------------------------------");
        receiptLines.add("Subtotal: " + subtotal.setScale(2, RoundingMode.HALF_UP));
        receiptLines.add("Discount: " + discountAmount.setScale(2, RoundingMode.HALF_UP));
        receiptLines.add("Total: " + finalTotal);
        receiptLines.add("Order ID: " + savedOrder.getOrderId());

        String receiptPath = generateReceiptFile(savedOrder.getOrderId(), receiptLines);

        CheckoutResponse checkoutResponse = new CheckoutResponse();
        checkoutResponse.setOrderId(savedOrder.getOrderId());
        checkoutResponse.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        checkoutResponse.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        checkoutResponse.setFinalTotal(finalTotal);
        checkoutResponse.setReceiptPath(receiptPath);

        return checkoutResponse;
    }

    private String generateReceiptFile(Long orderId, List<String> receiptLines) {
        try {
            Path outputDirectory = Paths.get(receiptOutputDirectory);
            Files.createDirectories(outputDirectory);

            String fileName = "receipt-order-" + orderId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".txt";
            Path filePath = outputDirectory.resolve(fileName);
            Files.write(filePath, receiptLines, StandardCharsets.UTF_8);
            return filePath.toAbsolutePath().toString();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to generate receipt file", exception);
        }
    }
}
