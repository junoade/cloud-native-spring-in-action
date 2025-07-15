# Chapter03 - Restful 애플리케이션 코딩

## Contents
|번호|Topic| 요약 |
|--|-------|-|
|1|클라우드 네이티브 프로젝트 부트스트래핑| |
|2|임베디드 서버로 작업| |
|3|스프링 MVC를 이용한 RESTful 애플리케이션 구축| |
|4|스프링 RESTful 애플리케이션 테스트| |
|5|배포 파이프라인: 빌드 및 테스트| |


## REST API Specs
| Endpoint           | HTTP Method | Request Body | Status | Response Body | Details                             |
|--------------------|-------------|--------------|--------|---------------|-------------------------------------|
| `/books`           | GET         |              | 200    | `Book[]`      |                                     |
| `/books`           | POST        | `Book`       | 201    | `Book`        | 카탈로그에 새 도서 추가            |
| `/books`           | POST        |              | 422    |               | 동일한 ISBN 도서가 이미 존재        |
| `/books/{isbn}`    | GET         |              | 200    | `Book`        | 주어진 isbn을 갖는 도서 조회       |
| `/books/{isbn}`    | GET         |              | 404    |               | 주어진 isbn을 갖는 도서가 존재하지 않음 |
| `/books/{isbn}`    | PUT         | `Book`       | 200    | `Book`        | 주어진 ISBN을 갖는 도서를 업데이트 |
| `/books/{isbn}`    | PUT         | `Book`       | 201    | `Book`        | 주어진 ISBN을 갖는 도서가 없을 땐 생성 |
| `/books/{isbn}`    | DELETE      |              | 204    |               | 주어진 ISBN 도서 삭제              |


## Summary

### Java Bean Validation
-- DTO 개체에 대한 입력값 검증 애노테이션 추가 / Controller 의 메소드에 @Valid 애노테이션
- 스프링부트 2.3 버전 이후부터는 spring-boot-starter-web 에 포함 안됨
- 
```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### 예외처리 코드 분리 및 응집력 높게 관리/ RestControllerAdvice

#### 구현
```java
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


```



- HTTP 요청 예
```
curl -X 'POST' \
  'http://localhost:9001/books' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "isbn": "11",
  "author": "string",
  "price": 0
}'

```



- HTTP 응답 예
```
Error: response status is 400

{
  "price": "The book price must be greater than zero",
  "isbn": "The ISBN format must be valid",
  "title": "The title must be defined"
}
```


## 자동화된 테스트의 중요성
- 자동화된 방식으로 코드를 테스트할 수 있다면, 자신있게 리팩토링을 할 수 있고
- 그에 따라 생산성, 속도와 지속적 전달 프로세스를 달성할 수 있다.

### 단위테스트
- spring-boot-starter-test 에서 JUnit5, Mockito, AssertJ 등의 테스트 라이브러리 제공
```
./gradlew test --tests BookValidationTests
```
```java

package com.polarbookshop.catalogservice.books.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("모든 입력 필드가 유효함")
    void whenAllFieldsAreValidThenSucceeds() {
        Book book = new Book("1234567890", "Title", "Author", 9.01);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("입력필드 ISBN이 유효하지 않음")
    void whenIsbnDefinedButInvalidThenFails() {
        Book book = new Book("a234567890", "Title", "Author", 9.01);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The ISBN format must be valid");
    }
}
```

### Mockito 관련
@InjectMocks의 Mock 객체 스캔 및 주입 범위

1. 같은 테스트 클래스 내의 @Mock, @Spy로 선언된 객체들만 대상
   •	@InjectMocks는 현재 테스트 클래스 내부에서 @Mock(또는 @Spy)로 생성된 객체들 중, 타입이 맞는 필드에 자동 주입

```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    // ...
}
```

2. 주입 방식은 필드 주입, 생성자 주입, 세터 주입 모두 허용 
- Mockito는 다음 우선순위로 주입을 시도합니다:
```md
1.	생성자 주입
2.	세터 메서드 주입
3.	필드 주입 (리플렉션)
      여러 생성자가 있다면, mock과 타입이 맞는 생성자를 우선적으로 사용합니다.
```

3. 다른 클래스에 정의된 @Mock 객체는 인식하지 못함
- @Mock 객체가 다른 테스트 클래스에 있거나, 외부에서 주입된 경우는 자동으로 인식되지 않습니다. 
- 반드시 같은 테스트 클래스 안에서 선언된 Mock이어야 합니다.


4. @InjectMocks는 DI 컨테이너(Spring 등)에서 관리되는 빈이 아니라, Mockito가 직접 생성한 테스트용 객체

## 통합테스트
- `@SpringBootTest` 어노테이션으로 애플리케이션 컨텍스트를 로드
- `webEnvironment` 속성으로 내장 톰캣 등 웹 환경 설정
- 필요한 경우 `@AutoConfigureMockMvc` 또는 `TestRestTemplate` 으로 HTTP API 호출
- `@Testcontainers` 등으로 외부 의존성(DB, Kafka 등) 컨테이너화하여 실제 환경에 가까운 테스트 수행

## 취약점 검사
```sh
brew tap anchore/grype
brew install grype

```

## Github Action


