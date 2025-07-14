package com.polarbookshop.catalogservice.books.service;


import com.polarbookshop.catalogservice.books.domain.Book;
import com.polarbookshop.catalogservice.books.domain.exception.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.books.domain.exception.BookNotFoundException;
import com.polarbookshop.catalogservice.books.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> viewBookList() {
        return bookRepository.findAll();
    }

    public Book viewBookDetails(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Book addBookToCatalog(Book book) {
        String isbn = book.isbn();
        if(bookRepository.existsByIsbn(isbn)) {
            throw new BookAlreadyExistsException(isbn);
        }
        return bookRepository.save(book);
    }

    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Book editBooksDetails(String isbn, Book book) {
        return bookRepository.findByIsbn(isbn)
                .map(prev -> {
                    var bookToEdit = new Book(
                            prev.isbn(),
                            book.title(),
                            book.author(),
                            book.price());
                    return bookRepository.save(bookToEdit);
                }).orElseGet(() -> addBookToCatalog(book));
    }
}
