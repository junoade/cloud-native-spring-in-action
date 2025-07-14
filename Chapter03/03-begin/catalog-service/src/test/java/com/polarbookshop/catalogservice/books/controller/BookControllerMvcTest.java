package com.polarbookshop.catalogservice.books.controller;

import com.polarbookshop.catalogservice.books.domain.exception.BookNotFoundException;
import com.polarbookshop.catalogservice.books.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerMvcTest {

    @Autowired
    // 톰캣 같은 서버를 로드하지 않고도 모의환경에서 웹 계층 테스트를 위한 유틸리티 클래스
    private MockMvc mockMvc;


    @MockitoBean
    private BookService bookService; // 스프링 애플리케이션 context에 BookService의 Mock 객체 추가

    @Test
    void whenGetBookNotExistingThenShouldReturnNotFound() throws Exception {
        String isbn = "73737313940";

        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);

        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }
}