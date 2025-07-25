package com.polarbookshop.catalogservice.domain;

import com.polarbookshop.catalogservice.config.DataConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integration")
class BookRepositoryJdbcTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    @DisplayName("이미 등록된 ISBN 도서 조회")
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "Publisher");

        jdbcAggregateTemplate.insert(book);
        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());
    }

    @Test
    @DisplayName("모든 도서 조회")
    void findAllBooks() {
        var book1 = Book.of("1234561235", "Title", "Author", 12.90, "Publisher");
        var book2 = Book.of("1234561236", "Another Title", "Author", 12.90, "Publisher");
        jdbcAggregateTemplate.insert(book1);
        jdbcAggregateTemplate.insert(book2);

        Iterable<Book> actualBooks = bookRepository.findAll();


        assertThat(StreamSupport.stream(actualBooks.spliterator(), true)
                .filter(book -> book.isbn().equals(book1.isbn()) || book.isbn().equals(book2.isbn()))
                .collect(Collectors.toList())).hasSize(2);
    }

    @Test
    @DisplayName("도서 존재시 참 반환")
    void existsByIsbnWhenExistingThenTrue() {
        var bookIsbn = "1234561239";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "Publisher");

        jdbcAggregateTemplate.insert(book);
        boolean isExists = bookRepository.existsByIsbn(bookIsbn);
        assertThat(isExists).isTrue();
    }

    @Test
    @DisplayName("도서 미존재시 거짓 반환")
    void existsByIsbnWhenNotExistingThenTrue() {
        var bookIsbn = "1234561239";
        bookRepository.deleteByIsbn(bookIsbn);
        boolean isExists = bookRepository.existsByIsbn(bookIsbn);
        assertThat(isExists).isFalse();
    }
}