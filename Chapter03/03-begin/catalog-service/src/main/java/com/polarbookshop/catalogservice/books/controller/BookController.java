package com.polarbookshop.catalogservice.books.controller;

import com.polarbookshop.catalogservice.books.domain.Book;
import com.polarbookshop.catalogservice.books.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        Iterable<Book> list = bookService.viewBookList();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        bookService.addBookToCatalog(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<?> getBook(@PathVariable("isbn") String isbn) {
        Book foundBook = bookService.viewBookDetails(isbn);
        return new ResponseEntity<>(foundBook, HttpStatus.OK);
    }

    @PutMapping("{isbn}")
    public ResponseEntity<?> updateBook(@PathVariable("isbn") String isbn, @RequestBody Book book) {
        Book updatedBook = bookService.editBooksDetails(isbn, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable("isbn") String isbn) {
        bookService.removeBookFromCatalog(isbn);
        return ResponseEntity.noContent().build();
    }

}
