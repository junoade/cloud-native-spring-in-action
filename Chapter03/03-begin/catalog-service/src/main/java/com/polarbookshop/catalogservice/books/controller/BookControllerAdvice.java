package com.polarbookshop.catalogservice.books.controller;

import com.polarbookshop.catalogservice.books.domain.exception.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.books.domain.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BookControllerAdvice {

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleBookNotFoundException(BookNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String handleBookAlreadyExistsException(BookAlreadyExistsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()                    // 1. 바인딩·검증 결과 객체 획득
                .getAllErrors()                 // 2. 모든 에러 객체 리스트(List<ObjectError>) 반환
                .forEach(error -> {  // 3. 각 에러에 대해 반복 처리
                    // 4. FieldError로 캐스팅하여, 어떤 필드에서 발생한 에러인지 가져옴
                    String fieldName = ((FieldError) error).getField();
                    // 5. 에러 메시지(기본 메시지) 획득
                    String errorMessage = error.getDefaultMessage();
                    // 6. 필드명(key) : 메시지(value) 형태로 Map 등에 저장
                    errors.put(fieldName, errorMessage);
                });
        return errors;
    }
}
