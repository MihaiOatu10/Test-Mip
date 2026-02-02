package org.acme.service;

import org.acme.entity.Book;
import org.acme.entity.Register;
import org.acme.entity.Sale;
import org.acme.entity.SaleItem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class SaleService {

    @Transactional
    public Sale createSale(SaleRequest request) {
        // Validate register (create if not exists)
        Register register = null;
        if (request.registerId != null) {
            register = Register.findById(request.registerId);
        }
        if (register == null) {
            // create a new register if id missing or not found
            register = new Register(request.registerName != null ? request.registerName : "default");
            register.persist();
        }

        // Validate book IDs
        List<Long> missing = new ArrayList<>();
        for (SaleItemRequest it : request.items) {
            if (Book.findById(it.bookId) == null) {
                missing.add(it.bookId);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing book IDs: " + missing);
        }

        Sale sale = new Sale();
        sale.registerId = register.id;

        BigDecimal total = BigDecimal.ZERO;
        for (SaleItemRequest it : request.items) {
            Book b = Book.findById(it.bookId);
            BigDecimal price = b.price != null ? b.price : BigDecimal.ZERO;
            BigDecimal qty = BigDecimal.valueOf(it.quantity);
            BigDecimal subtotal = price.multiply(qty);
            SaleItem si = new SaleItem(b.id, b.title, price, it.quantity, subtotal);
            sale.items.add(si);
            total = total.add(subtotal);
        }

        sale.total = total.setScale(2, RoundingMode.HALF_UP);
        sale.discount = calculateDiscount(sale.total);
        sale.total = sale.total.subtract(sale.discount).setScale(2, RoundingMode.HALF_UP);

        sale.persist();
        return sale;
    }

    private BigDecimal calculateDiscount(BigDecimal total) {
        if (total.compareTo(BigDecimal.valueOf(300)) > 0) {
            return total.multiply(BigDecimal.valueOf(0.07)).setScale(2, RoundingMode.HALF_UP);
        } else if (total.compareTo(BigDecimal.valueOf(200)) > 0) {
            return total.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public SalesReport getGlobalSalesReport() {
        List<Sale> sales = Sale.listAll();
        Map<Long, SoldBook> map = new HashMap<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Sale s : sales) {
            BigDecimal saleValue = s.total.add(s.discount); // original total before discount
            grandTotal = grandTotal.add(saleValue);
            for (SaleItem it : s.items) {
                SoldBook sb = map.get(it.bookId);
                if (sb == null) {
                    sb = new SoldBook(it.bookId, it.title, it.price, 0, BigDecimal.ZERO);
                    map.put(it.bookId, sb);
                }
                sb.quantity += it.quantity;
                sb.total = sb.total.add(it.subtotal);
            }
        }

        List<SoldBook> soldBooks = new ArrayList<>(map.values());
        soldBooks.sort(Comparator.comparingLong(a -> a.bookId));

        return new SalesReport(soldBooks, grandTotal.setScale(2, RoundingMode.HALF_UP));
    }

    // DTO-like inner classes to keep files minimal
    public static class SaleRequest {
        public Long registerId;
        public String registerName;
        public List<SaleItemRequest> items;

        public SaleRequest() {}
    }

    public static class SaleItemRequest {
        public Long bookId;
        public int quantity;

        public SaleItemRequest() {}
    }

    public static class SoldBook {
        public Long bookId;
        public String title;
        public BigDecimal unitPrice;
        public int quantity;
        public BigDecimal total;

        public SoldBook(Long bookId, String title, BigDecimal unitPrice, int quantity, BigDecimal total) {
            this.bookId = bookId;
            this.title = title;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.total = total;
        }
    }

    public static class SalesReport {
        public List<SoldBook> soldBooks;
        public BigDecimal grandTotal;

        public SalesReport(List<SoldBook> soldBooks, BigDecimal grandTotal) {
            this.soldBooks = soldBooks;
            this.grandTotal = grandTotal;
        }
    }
}

