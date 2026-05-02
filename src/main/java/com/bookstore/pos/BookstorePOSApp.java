package com.bookstore.pos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookstorePOSApp extends JFrame {
    private enum ThemeMode { LIGHT, DARK }

    private ThemeMode currentTheme = ThemeMode.LIGHT;
    private Color bg = new Color(245, 245, 247);
    private Color card = Color.WHITE;
    private Color text = new Color(28, 28, 30);
    private Color inputBg = Color.WHITE;
    private Color accent = new Color(0, 122, 255);
    private final Inventory inventory = new Inventory(5);
    private final List<Supplier> suppliers = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();
    private final ReceiptService receiptService = new ReceiptService();
    private final DecimalFormat money = new DecimalFormat("0.00");

    private JTextField searchField;
    private JTable inventoryTable;
    private JTable cartTable;
    private DefaultTableModel inventoryModel;
    private DefaultTableModel cartModel;
    private JTextArea lowStockArea;
    private JTextArea supplierArea;
    private JTextField customerNameField;
    private JCheckBox memberCheck;
    private JLabel totalLabel;
    private JPanel rootPanel;
    private JButton searchBtn;
    private JButton resetBtn;
    private JButton addToCartBtn;
    private JButton removeFromCartBtn;
    private JButton assignSupplierBtn;
    private JButton checkoutBtn;
    private JButton addBookBtn;
    private JButton restockBtn;
    private JComboBox<String> themeCombo;

    public BookstorePOSApp() {
        setupSampleData();
        setupLookAndFeel();
        buildUi();
        refreshInventoryTable(inventory.getAllBooks());
        refreshCartTable();
        refreshLowStockDashboard();
        refreshSupplierPanel();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        applyTheme(ThemeMode.LIGHT);
    }

    private void applyTheme(ThemeMode mode) {
        currentTheme = mode;
        if (mode == ThemeMode.DARK) {
            bg = new Color(22, 24, 28);
            card = new Color(36, 39, 45);
            text = new Color(245, 247, 250);
            inputBg = new Color(48, 52, 60);
            accent = new Color(64, 156, 255);
        } else {
            bg = new Color(245, 245, 247);
            card = Color.WHITE;
            text = new Color(28, 28, 30);
            inputBg = Color.WHITE;
            accent = new Color(0, 122, 255);
        }
    }

    private void buildUi() {
        setTitle("DigiLib Bookstore POS");
        setSize(1200, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(rootPanel);

        rootPanel.add(buildTopPanel(), BorderLayout.NORTH);
        rootPanel.add(buildCenterPanel(), BorderLayout.CENTER);
        rootPanel.add(buildRightPanel(), BorderLayout.EAST);
        rootPanel.add(buildBottomPanel(), BorderLayout.SOUTH);
        applyThemeToComponents();
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel title = new JLabel("Bookstore POS Dashboard");
        title.setFont(new Font("SF Pro Display", Font.BOLD, 26));
        panel.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        this.searchBtn = searchBtn;
        styleButton(searchBtn);
        searchBtn.addActionListener(e -> refreshInventoryTable(inventory.searchByTitleOrAuthor(searchField.getText())));
        JButton resetBtn = new JButton("Reset");
        this.resetBtn = resetBtn;
        styleButton(resetBtn);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            refreshInventoryTable(inventory.getAllBooks());
        });
        searchPanel.add(new JLabel("Search by Title/Author:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        themeCombo.addActionListener(e -> {
            applyTheme(themeCombo.getSelectedIndex() == 1 ? ThemeMode.DARK : ThemeMode.LIGHT);
            applyThemeToComponents();
        });
        searchPanel.add(new JLabel("Theme:"));
        searchPanel.add(themeCombo);
        panel.add(searchPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));

        inventoryModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "Stock", "Supplier"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        inventoryTable = new JTable(inventoryModel);
        panel.add(wrap("Inventory", new JScrollPane(inventoryTable)));

        cartModel = new DefaultTableModel(new String[]{"ID", "Title", "Qty", "Unit Price", "Line Total"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartModel);
        panel.add(wrap("Cart", new JScrollPane(cartTable)));

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(320, 0));

        JPanel customerPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        customerNameField = new JTextField("Walk-in Customer");
        memberCheck = new JCheckBox("Membership Discount (10%)");
        memberCheck.addActionListener(e -> refreshCartTable());
        memberCheck.setForeground(text);
        memberCheck.setOpaque(false);
        customerPanel.add(new JLabel("Customer Name"));
        customerPanel.add(customerNameField);
        customerPanel.add(memberCheck);

        lowStockArea = new JTextArea(8, 24);
        lowStockArea.setEditable(false);
        supplierArea = new JTextArea(8, 24);
        supplierArea.setEditable(false);

        JPanel reports = new JPanel(new GridLayout(2, 1, 0, 10));
        reports.add(wrap("Low-Stock Alerts", new JScrollPane(lowStockArea)));
        reports.add(wrap("Supplier Management", new JScrollPane(supplierArea)));

        panel.add(wrap("Customer", customerPanel), BorderLayout.NORTH);
        panel.add(reports, BorderLayout.CENTER);
        panel.add(buildActionsPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        addToCartBtn = new JButton("Add Selected Book to Cart");
        styleButton(addToCartBtn);
        addToCartBtn.addActionListener(e -> addSelectedBookToCart());
        removeFromCartBtn = new JButton("Remove Selected Cart Item");
        styleButton(removeFromCartBtn);
        removeFromCartBtn.addActionListener(e -> removeSelectedCartItem());
        checkoutBtn = new JButton("Checkout & Save Receipt");
        styleButton(checkoutBtn);
        checkoutBtn.addActionListener(e -> checkout());
        assignSupplierBtn = new JButton("Assign Supplier to Book");
        styleButton(assignSupplierBtn);
        assignSupplierBtn.addActionListener(e -> assignSupplier());
        addBookBtn = new JButton("Add New Book");
        styleButton(addBookBtn);
        addBookBtn.addActionListener(e -> addNewBook());
        restockBtn = new JButton("Restock Selected Book");
        styleButton(restockBtn);
        restockBtn.addActionListener(e -> restockSelectedBook());

        panel.add(addToCartBtn);
        panel.add(removeFromCartBtn);
        panel.add(assignSupplierBtn);
        panel.add(addBookBtn);
        panel.add(restockBtn);
        panel.add(checkoutBtn);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(totalLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel wrap(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(card);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(currentTheme == ThemeMode.DARK ? new Color(72, 72, 80) : new Color(229, 229, 234)),
                BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), title)
        ));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
    }

    private void applyThemeToComponents() {
        rootPanel.setBackground(bg);
        getContentPane().setBackground(bg);

        searchField.setBackground(inputBg);
        searchField.setForeground(text);
        customerNameField.setBackground(inputBg);
        customerNameField.setForeground(text);
        memberCheck.setForeground(text);
        lowStockArea.setBackground(inputBg);
        lowStockArea.setForeground(text);
        supplierArea.setBackground(inputBg);
        supplierArea.setForeground(text);

        inventoryTable.setBackground(inputBg);
        inventoryTable.setForeground(text);
        cartTable.setBackground(inputBg);
        cartTable.setForeground(text);

        for (JButton button : new JButton[]{searchBtn, resetBtn, addToCartBtn, removeFromCartBtn, assignSupplierBtn, addBookBtn, restockBtn, checkoutBtn}) {
            if (button == null) continue;
            button.setBackground(accent);
            button.setForeground(Color.WHITE);
            button.setEnabled(true);
        }

        repaint();
    }

    private void addNewBook() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        Supplier supplier = (Supplier) JOptionPane.showInputDialog(this, "Supplier", "Supplier",
                JOptionPane.PLAIN_MESSAGE, null, suppliers.toArray(), suppliers.get(0));
        if (supplier == null) return;

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Book ID (e.g. B006)")); panel.add(idField);
        panel.add(new JLabel("Title")); panel.add(titleField);
        panel.add(new JLabel("Author")); panel.add(authorField);
        panel.add(new JLabel("Price")); panel.add(priceField);
        panel.add(new JLabel("Initial Stock")); panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            if (id.isBlank() || title.isBlank() || author.isBlank() || stock < 0 || price < 0) throw new IllegalArgumentException();
            inventory.addBook(new Book(id, title, author, price, stock, supplier));
            refreshInventoryTable(inventory.getAllBooks());
            refreshLowStockDashboard();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid values.");
        }
    }

    private void restockSelectedBook() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a book to restock.");
            return;
        }
        String bookId = String.valueOf(inventoryModel.getValueAt(row, 0));
        Book book = inventory.getAllBooks().stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
        if (book == null) return;
        String qty = JOptionPane.showInputDialog(this, "Enter quantity to add:", "Restock " + book.getTitle(), JOptionPane.PLAIN_MESSAGE);
        if (qty == null) return;
        try {
            int addQty = Integer.parseInt(qty.trim());
            if (addQty <= 0) throw new IllegalArgumentException();
            book.setStock(book.getStock() + addQty);
            refreshInventoryTable(inventory.getAllBooks());
            refreshLowStockDashboard();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number.");
        }
    }

    private void addSelectedBookToCart() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a book first.");
            return;
        }
        String bookId = String.valueOf(inventoryModel.getValueAt(row, 0));
        Book book = inventory.getAllBooks().stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
        if (book == null || book.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Book is out of stock.");
            return;
        }

        book.setStock(book.getStock() - 1);
        CartItem existing = cart.stream().filter(ci -> ci.getBook().getId().equals(bookId)).findFirst().orElse(null);
        if (existing == null) cart.add(new CartItem(book, 1));
        else existing.setQuantity(existing.getQuantity() + 1);

        refreshInventoryTable(inventory.searchByTitleOrAuthor(searchField.getText().isBlank() ? "" : searchField.getText()));
        refreshCartTable();
        refreshLowStockDashboard();
    }

    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) return;
        String bookId = String.valueOf(cartModel.getValueAt(row, 0));
        CartItem item = cart.stream().filter(ci -> ci.getBook().getId().equals(bookId)).findFirst().orElse(null);
        if (item == null) return;

        item.getBook().setStock(item.getBook().getStock() + 1);
        if (item.getQuantity() == 1) cart.remove(item);
        else item.setQuantity(item.getQuantity() - 1);

        refreshInventoryTable(inventory.searchByTitleOrAuthor(searchField.getText().isBlank() ? "" : searchField.getText()));
        refreshCartTable();
        refreshLowStockDashboard();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }
        Customer customer = new Customer(customerNameField.getText().trim().isBlank() ? "Walk-in Customer" : customerNameField.getText().trim(), memberCheck.isSelected());
        Order order = new Order("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), customer, cart);

        try {
            Path file = receiptService.writeReceipt(order);
            JOptionPane.showMessageDialog(this, "Checkout successful. Receipt saved to: " + file.toAbsolutePath());
            cart.clear();
            refreshCartTable();
            refreshLowStockDashboard();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing receipt: " + ex.getMessage());
        }
    }

    private void assignSupplier() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a book first.");
            return;
        }
        Book book = inventory.getAllBooks().stream()
                .filter(b -> b.getId().equals(String.valueOf(inventoryModel.getValueAt(row, 0))))
                .findFirst().orElse(null);
        if (book == null) return;

        Supplier selected = (Supplier) JOptionPane.showInputDialog(this, "Select supplier", "Supplier Assignment",
                JOptionPane.PLAIN_MESSAGE, null, suppliers.toArray(), book.getSupplier());
        if (selected != null) {
            book.setSupplier(selected);
            refreshInventoryTable(inventory.searchByTitleOrAuthor(searchField.getText().isBlank() ? "" : searchField.getText()));
            refreshSupplierPanel();
        }
    }

    private void refreshInventoryTable(List<Book> books) {
        inventoryModel.setRowCount(0);
        for (Book b : books) {
            inventoryModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), "$" + money.format(b.getPrice()), b.getStock(), b.getSupplier().getName()});
        }
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        double total = 0;
        for (CartItem item : cart) {
            cartModel.addRow(new Object[]{item.getBook().getId(), item.getBook().getTitle(), item.getQuantity(), "$" + money.format(item.getBook().getPrice()), "$" + money.format(item.getLineTotal())});
            total += item.getLineTotal();
        }
        if (memberCheck != null && memberCheck.isSelected()) total *= (1 - Order.MEMBER_DISCOUNT_RATE);
        totalLabel.setText("Total: $" + money.format(total));
    }

    private void refreshLowStockDashboard() {
        StringBuilder sb = new StringBuilder();
        for (Book b : inventory.getLowStockBooks()) {
            sb.append("• ").append(b.getTitle()).append(" (stock: ").append(b.getStock()).append(")\n");
        }
        lowStockArea.setText(sb.isEmpty() ? "All good. No low-stock items." : sb.toString());
    }

    private void refreshSupplierPanel() {
        StringBuilder sb = new StringBuilder();
        for (Supplier s : suppliers) {
            sb.append("• ").append(s.getName()).append(" | ").append(s.getContact()).append("\n");
        }
        supplierArea.setText(sb.toString());
    }

    private void setupSampleData() {
        Supplier s1 = new Supplier("S01", "Penguin Logistics", "penguin@suppliers.com");
        Supplier s2 = new Supplier("S02", "Harper Distribution", "harper@suppliers.com");
        Supplier s3 = new Supplier("S03", "Classic House Supply", "classic@suppliers.com");
        suppliers.add(s1); suppliers.add(s2); suppliers.add(s3);

        inventory.addBook(new Book("B001", "The Great Gatsby", "F. Scott Fitzgerald", 12.50, 8, s1));
        inventory.addBook(new Book("B002", "1984", "George Orwell", 10.99, 4, s2));
        inventory.addBook(new Book("B003", "To Kill a Mockingbird", "Harper Lee", 11.75, 10, s2));
        inventory.addBook(new Book("B004", "Clean Code", "Robert C. Martin", 30.00, 3, s3));
        inventory.addBook(new Book("B005", "Effective Java", "Joshua Bloch", 36.00, 6, s3));
    }

    private boolean authenticate() {
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username")); panel.add(user);
        panel.add(new JLabel("Password")); panel.add(pass);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION && "admin".equals(user.getText().trim()) && "book123".equals(new String(pass.getPassword()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookstorePOSApp app = new BookstorePOSApp();
            if (!app.authenticate()) {
                JOptionPane.showMessageDialog(null, "Invalid credentials. Use admin / book123");
                System.exit(0);
            }
            app.setVisible(true);
        });
    }
}
