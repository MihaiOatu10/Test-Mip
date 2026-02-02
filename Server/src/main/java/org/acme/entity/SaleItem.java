package org.acme.entity;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class SaleItem {
    public Long bookId;
    public String title;
    public BigDecimal price;
    public int quantity;
    public BigDecimal subtotal;

    public SaleItem() {}

    public SaleItem(Long bookId, String title, BigDecimal price, int quantity, BigDecimal subtotal) {
        this.bookId = bookId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}