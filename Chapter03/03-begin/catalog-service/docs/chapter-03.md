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