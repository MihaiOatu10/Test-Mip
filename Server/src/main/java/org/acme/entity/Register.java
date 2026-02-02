package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Register extends PanacheEntity {
    public String name;

    public Register() {}
    public Register(String name) { this.name = name; }
}