package com.polarbookshop.catalogservice.demo;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("testdata")
public class BookDataLoader {
    private final BookRepository bookRepository;

    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 애플리케이션 시작 단계가 완료되어 ApplicationReadyEvent 가 발생하면 테스트 데이터를 생성함
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadTestData() {
        for (int i = 0; i < 99; i++) {
            String isbnPrefix = "12345678";
            String isbnSuffix;
            if (i < 10) {
                isbnSuffix = "0" + i;
            } else {
                isbnSuffix = String.valueOf(i);
            }
            Book book = new Book(isbnPrefix + isbnSuffix, "Spring Boot", "Spring Boot", 1000.0 + i);
            bookRepository.save(book);
        }
    }
}
