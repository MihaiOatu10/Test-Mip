package org.acme.service;

import org.acme.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import java.math.BigDecimal;

@ApplicationScoped
public class BookInitializer {

    void onStart(@Observes StartupEvent ev) {
        if (Book.count() == 0) {
            Book b1 = new Book("Clean Code", "Robert C. Martin", BigDecimal.valueOf(120));
            b1.persist();
            Book b2 = new Book("Effective Java", "Joshua Bloch", BigDecimal.valueOf(150));
            b2.persist();
            Book b3 = new Book("Design Patterns", "Gamma et al.", BigDecimal.valueOf(200));
            b3.persist();
        }
    }
}

