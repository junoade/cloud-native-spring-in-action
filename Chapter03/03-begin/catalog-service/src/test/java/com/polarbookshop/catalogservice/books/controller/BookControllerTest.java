package com.polarbookshop.catalogservice.books.controller;

import com.polarbookshop.catalogservice.books.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("HTTP POST 요청을 통해 새로운 ISBN 값에 대한 도서 정보 추가")
    void whenPostRequestThenBookCreated() {
        Book expectedBook = new Book("1231231231", "Title", "Author", 9.90);

        webTestClient.post()
                .uri("/books")
                .bodyValue(expectedBook) // HTTP Request Body 에 추가
                .exchange() // 요청 전송
                .expectStatus().isCreated()
                .expectBody(Book.class)
                .value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }
}