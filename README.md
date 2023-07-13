
---

## Testing the development of a specific feature in "essentory project"

회원 가입 시 유저 인증에 사용되는 휴대폰 인증.

본인 인증이 아닌 휴대폰 소유 인증만 진행하기 위해 SMS 벤더사 Vonage의 Verify User v1 사용.

[Vendors features](https://chinggin.tistory.com/1018)

환경
 * Java 17 이상
 * Kotlin 1.8.0
 * Spring Boot 3.1.1
 * H2 Database
 * Vonage Java SDK 7.6.0


다음과 같은 주요 의존성을 포함합니다:

Spring Boot Starter: Spring Boot 애플리케이션을 구성하는 데 필요한 기본 의존성을 제공합니다.

Spring Boot Starter Web: Spring MVC와 내장된 Tomcat 서버를 사용하여 웹 애플리케이션을 개발하는 데 필요한 의존성을 제공합니다.

Spring Boot Starter Data JPA: Spring Data JPA를 사용하여 데이터베이스와 상호 작용하는 데 필요한 의존성을 제공합니다.

Spring Boot Starter Validation: 데이터 유효성 검사를 위한 Spring Boot Starter 의존성입니다.

H2 Database: 관계형 데이터베이스 H2의 의존성입니다.

Kotlin Reflect: Kotlin 리플렉션을 사용하기 위한 의존성입니다.

Jackson Module Kotlin: Kotlin을 위한 Jackson 모듈의 의존성입니다.

Vonage Client: Vonage Java SDK를 사용하여 Vonage API와 상호 작용하는 데 필요한 의존성을 제공합니다.

---
