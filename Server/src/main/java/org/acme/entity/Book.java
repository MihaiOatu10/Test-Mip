package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Book extends PanacheEntity {
    public String title;
    public String author;
    public BigDecimal price;

    public Book() {}

    public Book(String title, String author, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }
}

