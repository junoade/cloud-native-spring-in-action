
| 목차                             |
| ------------------------------ |
| 클라우드 네이티브 시스템을 위한 데이터베이스       |
| 스프링 데이터에 대한 데이터 지속성 JDBC       |
| 스프링 및 테스트컨테이너로 데이터 지속성 테스트하기   |
| 플라이웨이를 통한 프로덕션 환경에서의 데이터베이스 관리 |
```bash
docker run -d --name polar-postgres -e POSTGRES_USER=user -e POSTGRES_PASSWORD=___@ -e POSTGRES_DB=polardb_catalog -p 5432:5432 postgres:14.4

```


- DB Connection 맺기
```bash
jdbc:postgresql://localhost:5432/polardb_catalog

Database: polardb_catalog
Username: user
Password: ___

```


### Spring Data JDBC vs Spring Data JPA
- Spring에서 데이터베이스를 다루는 기술**이지만, **철학, 구조, 기능이 꽤 다릅니다**
    - Data JDBC는: **Spring 버전의 MyBatis 같은 단순 매핑 도구**
    - JPA는: **ORM 기반의 도메인 모델 중심의 데이터 처리 방식**

| **항목**       | spring-boot-starter-data-jdbc | spring-boot-starter-data-jpa                |
| ------------ | ----------------------------- | ------------------------------------------- |
| 기반 기술        | Spring Data JDBC              | Spring Data JPA + Hibernate                 |
| 철학           | 단순한 객체 ↔ 테이블 매핑               | ORM 기반 객체 모델 중심 설계                          |
| 성능/예측성       | 단순 & 예측 가능 (쿼리 직접 실행)         | 자동 쿼리/캐시 등 편하지만 복잡함                         |
| 연관 관계        | 연관관계는 단순 (JOIN 자동 없음)         | 연관관계(OneToMany 등) 풍부하게 표현 가능                |
| Lazy loading | ❌ 없음 (모두 EAGER)               | ✅ 있음 (@OneToMany(fetch = FetchType.LAZY) 등) |
| 엔티티 생명주기     | 없음 (단순 객체)                    | 있음 (@PrePersist, @PostLoad, flush 등)        |
| 트랜잭션 전파      | Spring의 기본만 따름                | Hibernate 내부에도 개입 가능                        |
| 쿼리 제어        | 대부분 명시적 (SQL 느낌 강함)           | 쿼리 자동 생성 + JPQL 사용 가능                       |
| 적합한 경우       | 단순한 CRUD 서비스, 마이크로서비스         | 복잡한 도메인 설계, 복합 연관관계                         |



### 5.2

Book 객체에 대한 식별자 및 낙관적 락을 위한 버저닝
```java

package com.polarbookshop.catalogservice.domain;  
  
import org.springframework.data.annotation.Id;  
import org.springframework.data.annotation.Version;  
  
import javax.validation.constraints.NotBlank;  
import javax.validation.constraints.NotNull;  
import javax.validation.constraints.Pattern;  
import javax.validation.constraints.Positive;  
  
import static org.HdrHistogram.Version.version;  
  
public record Book (  
  
        @Id  
        Long id,  
  
        @NotBlank(message = "The book ISBN must be defined.")  
       @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")  
        String isbn,  
  
        @NotBlank(message = "The book title must be defined.")  
        String title,  
  
        @NotBlank(message = "The book author must be defined.")  
        String author,  
  
        @NotNull(message = "The book price must be defined.")  
        @Positive(message = "The book price must be greater than zero.")  
        Double price,  
  
        @Version  
        int version // 낙관적 락을 위해 사용되는 엔티티 버전 필드  
  
){  
        /**  
         * ID가 null 이고 버전이 0이면, 새로운 엔티티로 인식  
         * @param isbn  
         * @param title  
         * @param author  
         * @param price  
         * @param version  
         * @return  
         */  
        public static Book of(String isbn, String title, String author, Double price, int version) {  
                return new Book(null, isbn, title, author, price, 0);  
        }  
}
```


#### JPA 사용시,
Record 를 써서 불변 객체를 쓰는게 아니라, 가변객체를 사용함
- @Entity 애노테이션 활용
- javax.persistence 또는 버전3에선 jakarta.persistence 패키지의 @Id 및 @Version


### Auditing

@EnableJdbcAuditing 애너테이션 사용해서 퍼시스턴스 엔티티에 대한 Auditing 활성화

```java
package com.polarbookshop.catalogservice.config;  
  
import org.springframework.context.annotation.Configuration;  
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;  
  
@Configuration  
@EnableJdbcAuditing  
public class DataConfig {  
}
```


### CRUD
- @Modifiying :@Query가 SELECT가 아닌 DML(INSERT/UPDATE/DELETE)일 때 붙이는 애노테이션
    - @Query는 SELECT 쿼리만 지원
    - UPDATE, DELETE 같은 DML 쿼리를 쓰면 **실행되지 않거나 예외가 발생**
    - @Modifying은 단순히 **쿼리를 실행하게 해줄 뿐**, 트랜잭션을 열어주거나 커밋해주는 기능은 없음
        - JPA의 모든 변경 쿼리는 **트랜잭션 없이 실행하면 무효**
- @Transactional

```
### **Spring Data JDBC**

### **는 내부적으로 각 Repository 메서드 호출 시**

  

➡️ **스프링 트랜잭션 매니저(DataSourceTransactionManager)**를 통해

➡️ **자동으로 단일 쿼리 단위의 트랜잭션을 열고 커밋**합니다.

단일 메서드 안에서 여러 DB 작업이 **같은 트랜잭션으로 묶여야 하는 경우**엔 반드시 명시
```



## 5.3 테스트컨테이너
- 테스트를 위한 자바 라이브러리, 일회용 경량 컨테이너 제공
- 프로덕션 환경에서 사용되는 실제 지원 서비스와 동일한 서비스를 테스트에서도 사용 가능
- 통합테스트 구현 / 신뢰성 및 안정적 테스트

### 의존성 추가
```gradle
ext {  
    //
    set('testcontainersVersion', "1.17.3")  
}

dependencies {
	// ...
	testImplementation 'org.testcontainers:postgresql'
}

dependencyManagement {  
    imports {  
	    ...
       mavenBom "org.testcontainers:testcontainers-bom:${testcontainersVersion}" 
    }  
}

```

### 데이터 소스 지정하는 프로파일 작성

src/test/resources/application-integration.yml
``` yml
spring:  
  datasource:  
    url: jdbc:tc:postgresql:14.12:///
```


### 예제코드
```java

package com.polarbookshop.catalogservice.domain;  
  
import com.polarbookshop.catalogservice.config.DataConfig;  
import org.junit.jupiter.api.DisplayName;  
import org.junit.jupiter.api.Test;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;  
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;  
import org.springframework.context.annotation.Import;  
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;  
import org.springframework.test.context.ActiveProfiles;  
  
import java.util.Optional;  
  
import static org.assertj.core.api.Assertions.assertThat;  
  
@DataJdbcTest  
@Import(DataConfig.class)  
@AutoConfigureTestDatabase(  
        replace = AutoConfigureTestDatabase.Replace.NONE  
)  
@ActiveProfiles("integration")  
class BookRepositoryJdbcTest {  
  
    @Autowired  
    private BookRepository bookRepository;  
  
    @Autowired  
    private JdbcAggregateTemplate jdbcAggregateTemplate;  
  
    @Test  
    @DisplayName("이미 등록된 ISBN 도서 조회")  
    void findBookByIsbnWhenExisting() {  
        var bookIsbn = "1234561237";  
        var book = Book.of(bookIsbn, "Title", "Author", 12.90);  
  
        jdbcAggregateTemplate.insert(book);  
        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);  
  
        assertThat(actualBook).isPresent();  
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());  
    }  
}

```


## 5.4 플라이웨이를 통한 프로덕션 환경에서의 DB 관리

- 자바 생태계에서의 DB 변경사항 추적, 버전 관리 및 배포 를 위한 도구
    - 플라이웨이(Flyway) 와 리퀴베이스(Liquibase)

### 플라이웨이
- 버전 마이그레이션 : 고유한 버전으로 식별되고, 정확히 **한 번씩** /  **순서대로** 적용
    - 이미 수행된 마이그레이션에 문제 발생시, 실행 취소 마이그레이션(undo migration) 가능
    - 스키마, 테이블, 컬럼 및 시퀀스와 같은 관계형 객체의 생성,변경,삭제와 데이터 수정 가능
    - 실행 내역은 flyway_schema_history 테이블에 저장
```sql
CREATE TABLE users (
    id INT PRIMARY KEY,
    name VARCHAR(100)
);

```


- 반복 마이그레이션 : 체크섬이 바뀔될 때마다 반복적으로 적용
    - e.g) 프로시저 함수가 업데이트 된 경우, 체크섬이 달라지고, 최신 버전으로 마이그레이션
``` sql
CREATE OR REPLACE VIEW user_summary AS
SELECT id, name, created_at FROM users;
```

#### 요약

| **항목** | **버전 마이그레이션**       | **반복 마이그레이션**          |
| ------ | ------------------- | ---------------------- |
| 파일명 예시 | V1__create_user.sql | R__user_view.sql       |
| 적용 시점  | 최초 한 번만             | 내용이 바뀔 때마다             |
| 적용 순서  | 버전 순서대로             | 버전 마이그레이션 이후           |
| 용도     | 테이블, 컬럼 등 스키마 변경    | 뷰, 함수 등 정의가 자주 갱신되는 객체 |

### 적용
- build.gradle
```gradle

dependencies {
	implementation 'org.flywaydb:flyway-core'
}
```


#### 기존 소스 변경
- 스프링부트 애플리케이션 시작시, 초기화 스크립트 옵션 제거
```yml
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```


#### 플라이웨이 migration 디렉토리 생성 및 버전 마이그레이션 스크립트 작성
- (기본 탐색 경로) src/main/resources/db/migration
- (파일명) V1__Initial_schema.sql
```sql
CREATE TABLE book (  
      id          BIGSERIAL PRIMARY KEY NOT NULL,  
      author      VARCHAR(255) NOT NULL,  
      isbn        VARCHAR(255) UNIQUE NOT NULL,  
      price       float8 NOT NULL,  
      title       VARCHAR(255) NOT NULL,  
      created_date timestamp NOT NULL,  
      last_modified_date timestamp NOT NULL,  
      version     integer NOT NULL  
);
```

- 기존 postgresql 도커 파일 제거
    - PostgreSQL 컨테이너가 자동 생성한 볼륨에 의해 테이블 메타데이터(schema)는 유지되고 있었음
    - Anonymous Volume
```bash
docker inspect polar-postgres	

// "Mounts": [ { ... } ]
"Source":"/var/lib/docker/volumes/e81f21871f762eb0a58b95db4c4aa29fe2599a1f5f00d49aed681f0a85315e00/_data",
"Destination": "/var/lib/postgresql/data"

docker volume ls 

// local     e81f21871f762eb0a58b95db4c4aa29fe2599a1f5f00d49aed681f0a85315e00

```

- 따라서 완전 초기화를 위해 컨테이너와 볼륨을 함께 제거함
    - -f : force
    - -v : volumes
```bash
docker rm -fv polar-postgres

docker volume ls

// 없어진 것 확인
```

| 예시                     |                       |
| ---------------------- | --------------------- |
| docker rm -f <컨테이너명>   | 컨테이너만 강제로 삭제 (볼륨은 남음) |
| docker rm -fv <컨테이너명>  | 컨테이너 + 볼륨 모두 강제 삭제    |
| docker volume rm <볼륨명> | 이름 있는 볼륨 직접 삭제        |


- 앞서 작성한 도커 이미지 생성 명렁어 실행
```bash
docker run -d --name polar-postgres -e POSTGRES_USER=user -e POSTGRES_PASSWORD=Wnsgh97@ -e POSTGRES_DB=polardb_catalog -p 5432:5432 postgres:14.4
```

- 스프링부트 애플리케이션 실행 및 결과 확인
```bash
./gradlew bootRun
```

- flyway_schema_history
```
1	1	Initial schema	SQL	V1__Initial_schema.sql	-1386197475	user	2025-07-25 23:44:15.794	4	true
```

#### 신규요건으로 새로운 컬럼 추가 시
- 기존 테이블에 새로운 컬럼 추가시, 기존 데이터들은 해당 컬럼의 값을 반드시 갖지 않아도 되는 선택적인 컬럼이어야 한다.

- 다음과 같이 V2 쿼리를 작성한다.
```sql
ALTER TABLE book  
ADD COLUMN publisher varchar(255);
```

- Book 도메인 레코드에 새로운 필드 추가후 정적 팩토리 메소드 of를 수정한다
```java
/**  
 * 정적 팩토리 메소드를 사용하여 Book 객체를 생성  
 * @param isbn  
 * @param title  
 * @param author  
 * @param price  
 * @param publisher   
* @return  
 */  
public static Book of(String isbn, String title, String author, Double price, String publisher) {  
        return new Book(null, isbn, title, author, price, publisher, null, null, 0);  
}

```

- 관련 의존성 코드들을 수정한다 ... 헐

- 실행해보자
```bash
./gradlew bootRun
```

- DB 테이블 컬럼 추가 확인
```
id
author
isbn
price
title
created_date
last_modified_date
version
publisher
```


#### c.f) 필수값 필드인 경우
- nullable 필드 추가하고
- 기존 데이터에 기본 값 삽입한 다음
    - 자바 마이그레이션 방식으로
- 컬럼에 NOT NULL 제약조건 추가하도록

|**단계**|**설명**|**예시**|
|---|---|---|
|V3|nullable 상태로 컬럼 추가|ALTER TABLE book ADD COLUMN publisher VARCHAR(255);|
|V4|자바 마이그레이션으로 **기존 데이터에 기본값 삽입**|UPDATE book SET publisher = '미상';|
|V5|컬럼에 NOT NULL 제약 추가|ALTER TABLE book ALTER COLUMN publisher SET NOT NULL;|


- 자바마이그레이션 예시
```java
public class V4__Fill_publisher_column extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE book SET publisher = '미상' WHERE publisher IS NULL");
        }
    }
}
```


#### c.f) 플라이웨이로 db 변경만 관리하도록 전용 시스템 구성
> 플라이웨이 스크립트를 실행하는 **전용 마이그레이션 서버 또는 CI 파이프라인**을 구성하는 것은
> **대규모 서비스, 빅테크, 금융/공공기관에서도 이미 보편화된 방식**입니다.

- Flyway CLI로 운영 DB 마이그레이션 자동화하는 GitHub Actions 워크플로우 예제
- 마이그레이션용 도커 컨테이너 구성 템플릿
- 마이그레이션 서버 운영시 체크리스트 (접근권한, 롤백 전략 등)
