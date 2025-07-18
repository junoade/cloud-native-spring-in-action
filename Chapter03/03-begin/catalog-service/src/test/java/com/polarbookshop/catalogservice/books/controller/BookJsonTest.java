package com.polarbookshop.catalogservice.books.controller;

import com.polarbookshop.catalogservice.books.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookJsonTest {
    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerializeBook() throws Exception {
        Book book = new Book("1234567890", "Title", "Author", 9.90);

        JsonContent<Book> jsonContent = json.write(book);

        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());

        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());

        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());

        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());

    }

    @Test
    void testDeserializeBook() throws Exception {
        String content = """
                {
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "price": "9.90"
                }
                """;
        assertThat(json.parseObject(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book("1234567890", "Title", "Author", 9.90));

    }
}
