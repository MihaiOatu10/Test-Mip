package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Sale extends PanacheEntity {
    public Long registerId;

    @ElementCollection
    public List<SaleItem> items = new ArrayList<>();

    public BigDecimal total = BigDecimal.ZERO;
    public BigDecimal discount = BigDecimal.ZERO;

    @Temporal(TemporalType.TIMESTAMP)
    public Date createdAt = new Date();

    public Sale() {}
}

