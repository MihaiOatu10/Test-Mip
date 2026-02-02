package org.acme.service;

import org.acme.entity.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;

@ApplicationScoped
public class BookInitializer {

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (Book.count() == 0) {
            Book.persist(new Book("Clean Code", "Robert C. Martin", BigDecimal.valueOf(120)));
            Book.persist(new Book("Effective Java", "Joshua Bloch", BigDecimal.valueOf(150)));
            Book.persist(new Book("Design Patterns", "Gamma et al.", BigDecimal.valueOf(200)));
            Book.persist(new Book("The Pragmatic Programmer", "Andrew Hunt", BigDecimal.valueOf(180)));
            Book.persist(new Book("Introduction to Algorithms", "Cormen et al.", BigDecimal.valueOf(250)));
            Book.persist(new Book("Refactoring", "Martin Fowler", BigDecimal.valueOf(160)));
            Book.persist(new Book("Clean Architecture", "Robert C. Martin", BigDecimal.valueOf(140)));
            Book.persist(new Book("Domain-Driven Design", "Eric Evans", BigDecimal.valueOf(210)));
        }
    }
}