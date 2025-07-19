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


### 속성 설정에 대한 우선순위
- CLI 인수, JVM 시스템 속성, 외부화 설정을 통해 새로운 JAR 아티팩트를 만들 필요가 없음

1. CLI 인수로 전달되는 속성값
```bash
 java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --polar.greeting="Welcome to the catalog from CLI"
```

- 응답
```
GET localhost:9001/
Welcome to the catalog from CLI
```

2. JVM 시스템 속성을 통한 애플리케이션 구성
```bash
 java -Dpolar.greeting="Welcome to the catalog from JVM" -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
```
- 결과
```
Welcome to the catalog from JVM
```

## 4.3 스프링 클라우드 컨피그 서버서 중앙식 설정 관리
- 설정데이터에 대한 지속적 수정과 외부저장 공간 
- 실행 중인 애플리케이션에 대한 런타임내 변경된 설정 적용 
- 분산된 환경 등 멀티 인스턴스에 대한 동일한 설정 값 유지
- 스프링부트의 속싱이나 환경 변수는 설정 암호화 X, 안전한 관리방법 필요


### 실습) 깃을 통한 설정 데이터 저장 및 컨피그 서버 구성
1. spring-cloud-config-server 의존성 추가
```gradle
implementation 'org.springframework.cloud:spring-cloud-config-server'
```

2. 설정 서버 활성화 `EnableConfigServer`

3. 설정 서버 설정
- 내장 톰켓 서버에 대한 연결시간초과 및 쓰레드 풀
- git uri 설정
```yml
server:
  port: 8888
  tomcat:
    connection-timeout: 2s
    keep-alive-timeout: 15s
    threads:
      max: 50
      min-spare: 5


spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: https://github.com/junoade/config-repo-example
          default-label: main
          timeout: 5
          clone-on-start: true
          force-pull: true
          search-paths: catalog-service # Spring Config 깃헙 레포 구조 참고
```

### 실습) 스프링 클라우드 컨피그 클라이언트 적용
- 애플리케이션과 설정서버를 통합하기 위해 의존성 추가
- 애플리케이션의 application.yml에 설정 추가
- JAR 아티팩트로 패키징 후 프로파일 지정 하여 실행
```bash
java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

- 결과
```
  Welcome to the production catalog from the config server
```

- 애플리케이션 실행 로그 분석
```
2025-07-19T20:58:11.829+09:00  INFO 41347 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T20:58:11.830+09:00  INFO 41347 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Located environment: name=catalog-service, profiles=[default], label=null, version=43ca90236c755b61b0d892c66c09674db0212cf6, state=
2025-07-19T20:58:11.830+09:00  INFO 41347 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T20:58:11.830+09:00  INFO 41347 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Located environment: name=catalog-service, profiles=[prod], label=null, version=43ca90236c755b61b0d892c66c09674db0212cf6, state=
```

- 컨피그 서버 로그 분석
```
2025-07-19T20:58:11.729+09:00  INFO 40651 --- [config-service] [nio-8888-exec-2] o.s.c.c.s.e.NativeEnvironmentRepository  : Adding property source: Config resource 'file [/var/folders/y4/59db_d3j6c90d9kmrbccf3040000gn/T/config-repo-13199186765214931613/catalog-service/application-prod.yml]' via location 'file:/var/folders/y4/59db_d3j6c90d9kmrbccf3040000gn/T/config-repo-13199186765214931613/catalog-service/'
```

### 실습) 컨피그 서버 다운시 
- fail-fast : true 로 해놨고, retry 패턴 적용한 것 확인
- 로컬 환경이라면 fail-fast : false 지정하자
```
 ✘ junhochoi@Junhoui-MacBookPro  ~/Desktop/Studying Programing/Spring/2024_cloudNativeSpring/cloud-native-spring-in-action/Chapter04/04-begin/catalog-service  ↱ ch4-study ±  java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.3)

2025-07-19T21:07:28.274+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.275+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/default": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.275+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.275+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/default": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.276+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.276+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/default": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.276+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.276+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/default": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.277+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.277+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/default": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.278+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.278+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/default": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.278+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.278+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/prod": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.279+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.279+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/prod": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.280+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.280+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/prod": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.280+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.280+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/prod": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.281+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.281+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/prod": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.282+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Fetching config from server at : http://localhost:8888
2025-07-19T21:07:28.282+09:00  INFO 41917 --- [catalog-service] [           main] o.s.c.c.c.ConfigServerConfigDataLoader   : Exception on Url - http://localhost:8888:org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8888/catalog-service/prod": Connection refused. Will be trying the next url if available
2025-07-19T21:07:28.340+09:00 ERROR 41917 --- [catalog-service] [           main] o.s.boot.SpringApplication               : Application run failed

org.springframework.cloud.config.client.ConfigClientFailFastException: Could not locate PropertySource and the fail fast property is set, failing
```

### 실습) 런타임시 새로운 설정 데이터 적용 과정
1) 새로운 설정 데이터를 원격 깃 저장소로 푸시
2) 해당 이벤트 후 스프링부트 애플리케이션에 HTTP POST /actuator/refresh 요청 전송
3) config server로 새로운 설정 데이터 요청
4) config server는 설정 저장소에서 pull 받아와서 최신 변경 설정 데이터 반환
5) 스프링부트 애플리케이션은 새 설정 데이터로 빈을 재로드
