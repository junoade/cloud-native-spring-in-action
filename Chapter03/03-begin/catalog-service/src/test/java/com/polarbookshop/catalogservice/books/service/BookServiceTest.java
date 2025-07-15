package com.polarbookshop.catalogservice.books.service;

import com.polarbookshop.catalogservice.books.domain.Book;
import com.polarbookshop.catalogservice.books.domain.exception.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.books.domain.exception.BookNotFoundException;
import com.polarbookshop.catalogservice.books.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("중복된 ISBN 도서 추가시 BookAlreadyExistsException 예외 발생")
    void whenBookToCreateButAlreadyExistsThenThrows() {
        String isbn = "1234567890";
        Book newBook = new Book(isbn, "Title", "author", 1000.0);

        when(bookRepository.existsByIsbn(isbn)).thenReturn(true);
        assertThatThrownBy(() -> bookService.addBookToCatalog(newBook))
                .isInstanceOf(BookAlreadyExistsException.class);
                // .hasMessage("A book with ISBN " + isbn + " already exists")
    }

    @Test
    @DisplayName("존재하지 않는 ISBN 도서 조회시 BookNotFoundException 예외 발생")
    void whenBookToReadButNotExistThenThrows() {
        String isbn = "1234567890";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.viewBookDetails(isbn))
                .isInstanceOf(BookNotFoundException.class);
    }
}