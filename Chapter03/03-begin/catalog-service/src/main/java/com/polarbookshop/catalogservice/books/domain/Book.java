package com.polarbookshop.catalogservice.books.domain;

public record Book(
        String isbn,
        String title,
        String author,
        Double price
) {}
