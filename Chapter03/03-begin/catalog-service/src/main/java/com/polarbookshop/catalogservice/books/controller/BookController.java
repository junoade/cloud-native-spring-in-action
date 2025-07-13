package com.polarbookshop.catalogservice.books.controller;

import com.polarbookshop.catalogservice.books.domain.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        // TODO
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        // TODO
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<?> getBook(@PathVariable("isbn") String isbn) {
        // TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("{isbn}")
    public ResponseEntity<?> updateBook(@PathVariable("isbn") String isbn, @RequestBody Book book) {
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable("isbn") String isbn) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
