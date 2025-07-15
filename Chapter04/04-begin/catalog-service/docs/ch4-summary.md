# CHAPTER 4 외부화 설정 관리

## Contents.
- 스프링 설정 : 속성과 프로파일
  - 설정 : 배포사이에 변경될 가능성이 있는 모든 것 / 스프링에서는 Enviroment라는 추상화 기능 제공
    - 설정소스에 상관없이 모든 설정 데이터 액세스 가능
  - 속성(Property) : 애플리케이션에서 설정 가능한 데이터를 정의하기 위해 사용한 키-값 쌍
    - e.g) db 설정, 로그 전략, 서버 타임아웃 값 등
  - 프로파일(Profile) : 스프링 빈의 논리적 그룹, 주어진 프로파일이 활성화될 때만 스프링 애플리케이션 컨텍스트에 포함됨
    - 1) 기능 플래그로서 활용
      > 특정 플랫폼에서의 인프라 관련 이슈를 처리하는 빈 그룹; 
      > - e.g) 쿠버네티스 환경에서 배포될 때만 로드해야하는 스프링 빈 그룹
      > - e.g) 테스트 환경에서 테스트 데이터 로드 하는 스프링 빈 그룹
    - 2) 프로파일을 설정 그룹으로 사용
      - 다시 말해, 설정 데이터를 그룹화하기 위해 사용
      > 특정 프로파일이 활성화된 경우에만 로드하는 설정 데이터 정의
      > - e.g) application-dev.yml; dev 프로파일이 활성화될 때만 스프링부트가 해당 파일의 속성 값들을 사용; application.yml보다 우선화
      - 소스코드와 같은 번들로 제공될 수도 있고, 애플리케이션 외부에 설정 서버를 두어 분리할 수도 있다. e.g) 스프링 클라우드 컨피그

- 외부화된 구성: 하나의 빌드, 여러 설정
- 스프링 클라우드 컨피그 서버로 중앙식 설정 관리하기
- 스프링 클라우드 컨피그 클라이언트로 설정 서버 사용


## 실습

### 스프링 속성에 접근 하는 방법 - @ConfigurationProperties


- 프로젝트 빌드시 새로운 사용자 정의 속성에 대한 메타데이터를 json방식으로 변환
- 앞서 @ConfigurationProperties
```json
{
  "groups": [
    {
      "name": "polar",
      "type": "com.polarbookshop.catalogservice.config.PolarProperties",
      "sourceType": "com.polarbookshop.catalogservice.config.PolarProperties"
    }
  ],
  "properties": [
    {
      "name": "polar.greeting",
      "type": "java.lang.String",
      "description": "사용자 정의 속성인 polar.greeting 속성에 대한 문자열 필드",
      "sourceType": "com.polarbookshop.catalogservice.config.PolarProperties"
    }
  ],
  "hints": []
}
```

annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor' 를 추가해서

@ConfigurationProperties을 붙여 사용자정의 설정한 메타데이터를 json 형식으로 변환


### 프로파일을 기능 플래그로서 사용
- testdata 로드 기능에 대한 프로파일 작성 후 bootRun 명령어로 실행

```bash
./gradlew bootRun

> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.4.3)

2025-07-15T23:54:57.907+09:00  INFO 11407 --- [           main] c.p.c.CatalogServiceApplication          : Starting CatalogServiceApplication using Java 17.0.15 with PID 11407 (/Users/junhochoi/Desktop/Studying Programing/Spring/2024_cloudNativeSpring/cloud-native-spring-in-action/Chapter04/04-begin/catalog-service/build/classes/java/main started by junhochoi in /Users/junhochoi/Desktop/Studying Programing/Spring/2024_cloudNativeSpring/cloud-native-spring-in-action/Chapter04/04-begin/catalog-service)
2025-07-15T23:54:57.908+09:00  INFO 11407 --- [           main] c.p.c.CatalogServiceApplication          : The following 1 profile is active: "testdata"
2025-07-15T23:54:58.144+09:00  INFO 11407 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 9001 (http)
2025-07-15T23:54:58.148+09:00  INFO 11407 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-07-15T23:54:58.148+09:00  INFO 11407 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.36]
2025-07-15T23:54:58.161+09:00  INFO 11407 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-07-15T23:54:58.161+09:00  INFO 11407 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 241 ms
2025-07-15T23:54:58.262+09:00  INFO 11407 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 9001 (http) with context path '/'
2025-07-15T23:54:58.265+09:00  INFO 11407 --- [           main] c.p.c.CatalogServiceApplication          : Started CatalogServiceApplication in 0.49 seconds (process running for 0.579)

```

GET 요청보내서 list를 받아와보자
```json
[
    {
        "isbn": "1234567809",
        "title": "Spring Boot",
        "author": "Spring Boot",
        "price": 1009.0
    },
    {
        "isbn": "1234567808",
        "title": "Spring Boot",
        "author": "Spring Boot",
        "price": 1008.0
    },
  ...
]    
```