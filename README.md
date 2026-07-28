# Transmate

비즈니스 회의를 위한 실시간 통번역 및 회의 관리 서비스입니다.

> 이 저장소는 2023년 3인 팀 캡스톤 프로젝트
> [2023capstone](https://github.com/minjuko/2023capstone)을 기반으로,
> 제가 담당한 백엔드와 AWS 번역 영역을 중심으로 재구성한 포트폴리오 버전입니다.
> 원본 팀 프로젝트의 커밋 이력과 작성자 정보는 보존했으며,
> 2026년 이후의 리팩터링·테스트·문서화는 개인 작업입니다.

## 프로젝트 구분

| 구분 | 기간 | 내용 |
| --- | --- | --- |
| 원본 팀 프로젝트 | 2023.03–2023.06 | React Native, Spring Boot, AWS 기반 캡스톤 프로젝트 |
| 포트폴리오 개선 | 2026.07– | 백엔드 구조 개선, 보안 보완, 테스트 및 CI·문서화 |

- 원본 저장소: [minjuko/2023capstone](https://github.com/minjuko/2023capstone)
- 포트폴리오 저장소: [minjuko/transmate](https://github.com/minjuko/transmate)
- 팀 구성: 모바일 1명, 백엔드 2명

## 담당 범위

### 팀 프로젝트 당시

- AWS 서버 환경 구축 및 Spring Boot 백엔드 배포
- Amazon Translate 연동과 Translate API 구현 참여
- 분야별 번역 데이터셋 및 AWS Custom Terminology 제작 참여

### 포트폴리오 개선

- Translate API를 Controller, Service, Gateway, AWS adapter로 분리
- 모바일과 백엔드의 API 계약 정합성 및 DTO 입력 검증 개선
- AWS 자격 증명·리전·제한시간·재시도 설정 외부화
- Firebase 인증과 사용자 데이터 소유권 검증 추가
- Flyway 마이그레이션 및 JPA 통합 테스트 구축
- AWS 오류를 안전한 `400`·`503` 응답으로 표준화
- 백엔드·모바일 테스트와 GitHub Actions CI 구성

모바일은 다른 팀원이 담당한 영역이므로 API 호환성, 인증 헤더, 환경 설정과
테스트에 필요한 최소 범위만 수정했습니다.

## 주요 개선 구조

```text
TranslateController
  → TranslateService
  → TranslationGateway
  → AwsTranslationGateway
  → Amazon Translate
```

- Controller는 HTTP 계약과 입력 검증을 담당합니다.
- Service는 번역 순서와 Custom Terminology 후처리를 담당합니다.
- Gateway는 외부 번역 공급자와 애플리케이션의 경계를 정의합니다.
- AWS adapter는 SDK 요청 변환과 공급자 오류 분류를 담당합니다.

실제 AWS 자격 증명이나 네트워크 없이도 번역 흐름과 장애 처리를 테스트할 수
있도록 외부 서비스 경계를 분리했습니다.

## 기술 스택

- Mobile: React Native, Firebase Authentication
- Backend: Java 17, Spring Boot, Spring Data JPA, Bean Validation
- Data: H2, Flyway
- Cloud: Amazon Translate, AWS SDK
- Test/CI: JUnit, Mockito, MockMvc, Jest, ESLint, GitHub Actions

## 실행 환경 설정

### Backend

| 환경변수 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `DB_URL` | 예 | 없음 | JDBC 데이터베이스 URL |
| `DB_USERNAME` | 예 | 없음 | 데이터베이스 사용자명 |
| `DB_PASSWORD` | 예 | 없음 | 데이터베이스 비밀번호 |
| `DB_DRIVER_CLASS_NAME` | 아니요 | `org.h2.Driver` | JDBC 드라이버 |
| `DB_DIALECT` | 아니요 | `org.hibernate.dialect.H2Dialect` | Hibernate dialect |
| `DB_DDL_AUTO` | 아니요 | `validate` | Hibernate 스키마 정책 |
| `H2_CONSOLE_ENABLED` | 아니요 | `false` | H2 콘솔 활성화 여부 |
| `H2_CONSOLE_ALLOW_OTHERS` | 아니요 | `false` | H2 콘솔 외부 접속 허용 |
| `AWS_TRANSLATE_REGION` | 아니요 | `ap-northeast-2` | Amazon Translate 리전 |
| `AWS_TRANSLATE_CONNECTION_TIMEOUT_MILLIS` | 아니요 | `3000` | AWS 연결 제한시간(ms) |
| `AWS_TRANSLATE_SOCKET_TIMEOUT_MILLIS` | 아니요 | `10000` | AWS 응답 제한시간(ms) |
| `AWS_TRANSLATE_MAX_ERROR_RETRY` | 아니요 | `2` | AWS 최대 재시도 횟수 |

Firebase Admin과 AWS SDK 자격 증명은 설정 파일에 저장하지 않고 각 SDK의 기본
자격 증명 체인으로 제공합니다.

```powershell
$env:DB_URL="jdbc:h2:tcp://localhost/~/local"
$env:DB_USERNAME="REDACTED"
$env:DB_PASSWORD="비밀번호"
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\firebase-service-account.json"
Set-Location backend
.\gradlew.bat bootRun
```

### Mobile

`mobile/.env.example`을 `mobile/.env`로 복사하고 환경에 맞게 설정합니다.
실제 `.env` 파일은 Git에 포함하지 않습니다.

```dotenv
BACKEND_API_URL=http://localhost:8080
GOOGLE_TRANSLATE_API_KEY=
GOOGLE_SPEECH_API_KEY=
KAKAO_REST_API_KEY=
```

모바일 앱 번들에 포함되는 API 키는 완전한 비밀이 될 수 없으므로 공급자
콘솔에서 앱·API·사용량 제한을 함께 설정해야 합니다.

## 문서

- [포트폴리오 상세](docs/PORTFOLIO.md)
- [Translate API 설계](docs/TRANSLATE_API.md)
- [AWS 인프라 정리](docs/AWS_INFRASTRUCTURE.md)
- [데이터셋 및 Custom Terminology](docs/DATASET.md)

## 커밋 이력 안내

기본 브랜치에는 다음 두 이력이 함께 보존되어 있습니다.

```text
2023capstone 팀 커밋 ─┐
                     ├─ 원본 팀 프로젝트 이력 연결
transmate 개선 커밋 ─┘
```

이력 연결 커밋은 기존 팀 저장소와 독립적으로 진행된 포트폴리오 저장소의
관계를 기록하기 위한 것입니다. 해당 커밋은 현재 포트폴리오 소스 트리를
변경하지 않으며, 각 커밋의 원래 작성자와 작성 시점을 유지합니다.

## 현재 한계

- 실제 데이터셋의 출처·정제 수치와 당시 AWS 인프라 상세 기록은 남아 있는
  자료만으로 검증 가능한 범위에서 문서화했습니다.
- 운영 DB는 프로젝트 범위를 고려해 H2와 Flyway 구성을 유지합니다.
- 상용 트래픽, 고가용성 또는 무중단 배포를 목표로 한 프로젝트는 아닙니다.
