# Transmate Backend

Transmate의 사용자 인증, 회의록·일정 관리, Amazon Translate 연동을 담당하는
Spring Boot REST API입니다. 서비스 전체 소개는 [루트 README](../README.md)를
참고하세요.

## Stack

- Java 17, Spring Boot 3
- Spring Data JPA, H2, Flyway
- Firebase Admin SDK
- AWS SDK for Amazon Translate
- JUnit 5, Mockito, MockMvc

## Structure

```text
auth/          Firebase 인증과 소유권 검사
account/       사용자 계정
meeting/       회의록 CRUD
schedule/      일정 CRUD
translation/   번역 로직과 AWS adapter
api/           공통 오류 응답과 입력 검증
resources/db/  Flyway 마이그레이션
```

## Configuration

| 환경변수 | 필수 | 기본값 |
| --- | --- | --- |
| `DB_URL` | 예 | 없음 |
| `DB_USERNAME` | 예 | 없음 |
| `DB_PASSWORD` | 예 | 없음 |
| `DB_DDL_AUTO` | 아니요 | `validate` |
| `AWS_TRANSLATE_REGION` | 아니요 | `ap-northeast-2` |
| `AWS_TRANSLATE_CONNECTION_TIMEOUT_MILLIS` | 아니요 | `3000` |
| `AWS_TRANSLATE_SOCKET_TIMEOUT_MILLIS` | 아니요 | `10000` |
| `AWS_TRANSLATE_MAX_ERROR_RETRY` | 아니요 | `2` |

운영 실행에는 Firebase Admin과 AWS SDK 자격 증명이 필요합니다. 자격 증명은
저장소에 저장하지 않고 각 SDK의 기본 자격 증명 방식으로 주입합니다.

## Run

```powershell
$env:DB_URL="jdbc:h2:mem:transmate;DB_CLOSE_DELAY=-1"
$env:DB_USERNAME="sa"
$env:DB_PASSWORD=""

.\gradlew.bat bootRun
```

Firebase 없이 서버 기동과 공개 API만 확인할 때:

```powershell
.\gradlew.bat bootRun --args="--transmate.auth.enabled=false"
```

## Test

```powershell
.\gradlew.bat clean test
```

API 계약, 인증·소유권, 번역 로직, AWS adapter, Flyway와 JPA CRUD를
검증합니다. 2026-08-10 기준 55개 테스트가 통과합니다.
