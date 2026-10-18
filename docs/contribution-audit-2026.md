# TransMate 개발 기여 및 검증 감사 (2026)

## 감사 기준

- 감사일: 2026-10-14 (Asia/Seoul)
- 대상: 현재 `main`(`a8d4b4c`)과 연결된 전체 Git 이력, `team/master`의 2023년 원본 이력
- 방법: 현재 코드·테스트·README·Git 객체/커밋을 교차 확인했다. 작성자 이름만으로 단독 구현을 판정하지 않았다.
- 기여자 식별: 이 문서에서 **본인**은 Git의 `minjuko <rhalswn0526@gmail.com>`와 `고민주 <83636444+minjuko@users.noreply.github.com>`를 뜻한다. 두 계정이 같은 사람이라는 판단은 저장소의 역할 문서와 계정명에 근거하며, 별도 신원 증명은 없다.
- 상태: 조사 시작 전 작업 트리는 깨끗했다. 감사 과정에서 제품 코드는 수정하지 않았다.
- 등급: **A 개인 주도**, **B 팀 공동**, **C 단순 참여/검토**, **D 확인 불가**. A/B는 구현 범위를 뜻하며 프로젝트 전체의 단독 소유를 뜻하지 않는다.

## 1. 프로젝트 개요와 실제 아키텍처

TransMate는 2023년 3인 캡스톤 프로젝트로 기록된 모바일 통번역 서비스다. React Native 앱이 Firebase와 Spring Boot API를 사용하고, 백엔드는 H2에 회의록·일정·계정을 저장하며 Amazon Translate에 번역을 위임한다. 전문용어는 직접 학습한 모델이 아니라 Amazon Translate **Custom Terminology**에 넘기는 용어집이다. 프로젝트 당시 EC2에 백엔드를 실행했다고 문서화되어 있으나 현재 공개 운영 중인 서비스는 아니다.

| 구성 요소 | 저장소에서 확인한 역할 | 연결 |
| --- | --- | --- |
| React Native 0.71.8 | 로그인, 채팅형 번역, STT, 회의록·일정 UI, PDF, 요약 | Spring REST API, Firebase Auth/Firestore, Google STT/감지, Kakao KoGPT |
| Spring Boot 3.0.6 / Java 17 | 계정·회의록·일정 CRUD, Firebase 토큰/소유권 검사, 번역 요청 중계 | JPA/H2, Amazon Translate |
| H2 + Flyway | `Account`, `Meeting`, `Schedule` 영속화 및 스키마 마이그레이션 | Spring Data JPA |
| Firebase | 모바일 인증·채팅 저장; 백엔드는 ID token 검증 | 모바일 및 Spring interceptor |
| Amazon Translate | 텍스트 번역과 적용 용어 반환 | Spring의 AWS SDK v1 adapter |
| Custom Terminology | SW·금융·항만/무역 분야 source-target 용어 적용 | 모바일의 분야 선택 → `/translate` → AWS 요청 |
| AWS EC2 | 프로젝트 당시 Spring Boot 실행 환경으로 사용했다고 역할 문서에 기록 | 모바일 → EC2의 Spring API → Translate |
| Kakao KoGPT | 모바일에서 회의 내용 요약 API를 직접 호출 | Spring 백엔드를 거치지 않음 |

실제 번역 흐름은 `ChattingScreen` → `mobile/lib/translationApi.js`(10초 제한, `/translate`) → `TranslateController` → `TranslateService` → `TranslationGateway` → `AwsTranslationGateway` → Amazon Translate 순서다. AWS가 돌려준 적용 용어가 있으면 서비스가 원문의 모든 해당 용어를 대괄호로 표시해 두 번째 번역을 요청하고, 번역문의 표식을 제거한다.

저장소에는 별도의 북마크·즐겨찾기·알림 구현이 없다. 회의록 저장과 일정 기능을 이를 대신하는 기능으로 확대 해석하면 안 된다.

## 2. 개인 주도·공동 구현·미확인 기여 구분

| 대상 | 등급 | 판정과 근거 |
| --- | --- | --- |
| 전문용어 데이터셋 구축(2023) | A, 단 증거 제한 | 현재/보관 README가 본인의 주도 역할로 일관되게 기록하고 예시 이미지가 남아 있다. 그러나 원본 데이터와 2023년 작성 커밋은 없어 행 단위 제작자를 독립 검증할 수 없다. |
| EC2 실행 환경 구성·배포(2023) | A, 단 증거 제한 | 현재/보관 README와 역할표가 본인 주도로 기록한다. EC2/IaC/스크립트/운영 로그가 없어 세부 설정과 실제 기간은 독립 검증할 수 없다. |
| Amazon Translate·Custom Terminology 연동(2023) | B | 역할 문서는 공동 구현으로 명시한다. 실제 2023 번역 코드의 작성자는 `sooobb`이며 커밋 `08ee938`, 일반 문장 오류 수정은 `557be7f`다. 본인의 코드 단독 구현으로 표현할 수 없다. |
| Spring Boot 백엔드 원형(2023) | B 또는 D | 팀 산출물임은 확인되지만 현재 연결된 2023 백엔드 커밋은 대부분 `sooobb` 작성이다. 본인의 구체적 코드 작성 범위는 확인 불가하다. |
| React Native/Firebase 원형(2023) | C 또는 D | 역할 문서상 다른 팀원 담당이다. 본인의 구체적 구현 커밋은 확인되지 않는다. |
| Spring 계층 분리·인증/인가·검증·DB 마이그레이션·예외 응답(2026) | A(후속 개선) | 본인 명의의 세분화된 커밋과 현재 코드·테스트가 일치한다. 2023 팀 프로젝트 당시 구현으로 소급할 수 없다. |
| 번역 gateway 분리·장애 매핑·용어 후처리 보강(2026) | A(후속 개선) | `ccc06b0`, `26af082`, `f029bd0`, `b7b4842`, `763186f`과 관련 테스트로 확인된다. |
| 테스트·CI·모바일 API 안정성 보강(2026) | A(후속 개선) | `4d9270b`, `9ce96a1`, `a4772d1`, `f8bd7a5`, `763186f` 등으로 확인된다. |

커밋 수는 본인 계정 64개(`minjuko` 52, `고민주` 12), `sooobb` 25개, `Hwang SooYeon` 12개로 집계되지만, merge·문서 커밋을 포함하므로 기여율로 환산하지 않는다.

## 3. 전문용어 데이터셋 구축 근거

### 확인된 사실

- 역할 문서는 공공 전문용어 데이터에서 서비스에 필요한 용어를 선별하고, 표기 형식을 통일해 SW·금융·항만/무역으로 분류한 뒤 `Source`/`Target` 쌍으로 만들었다고 설명한다.
- 예시 이미지 `docs/images/readme/dataset_example.png`에는 각 분야 6개씩 **총 18개 예시 행**이 보인다. 이는 전체 데이터셋 크기가 아니라 공개된 샘플 수다.
- 예시는 `FTP → 파일 전송 규약`, `ABS → 자산담보부증권`, `B/L → 선하증권`처럼 영문 원어/약어와 한국어 대응어의 두 열 구조다.
- 앱은 분야 값을 `TerminologyNames`로 백엔드에 보내고, AWS adapter는 값이 비어 있지 않을 때만 `withTerminologyNames`를 호출한다. 데이터 자체는 H2/JPA에서 조회하지 않으며 AWS Custom Terminology에 저장된 것으로 설계되어 있다.

### 확인되지 않은 사실

- Git 전체 객체에 CSV/TSV/XLS/XLSX 원본이나 생성·전처리 스크립트가 없다. 따라서 전체 행 수, 원 출처 URL/라이선스, 수집 일자, 중복 제거 규칙, 누락 처리 규칙, 정규화 알고리즘을 확인할 수 없다.
- 데이터 업로드 명령·AWS terminology 리소스 식별자·버전 이력도 없다.
- 2023년 원본 저장소에는 데이터셋 관련 커밋이 없다. 예시 이미지는 2026년 `3ec2d3d`에서 추가되었다.
- 정확도, BLEU, 사용자 평가, 번역 품질 개선율을 계산한 실험 자료가 없다. 그런 수치를 성과로 쓰면 안 된다.
- 현재 단위 테스트는 용어 적용 결과의 후처리 로직을 검증하지만 실제 데이터셋 품질이나 실 AWS 번역 품질을 측정하지 않는다.

## 4. Spring Boot 및 API 구현 근거

| 기능 | Controller / Service / Repository / 모델 | 외부 호출·예외·테스트 | 시점·기여 판정 |
| --- | --- | --- | --- |
| 전문용어 번역 | `TranslateController` → `TranslateService`; `TranslateRequest`, `TranslationCommand/Result/Term`; DB repository 없음 | AWS gateway가 적용 용어를 변환하고 서비스가 빈/중복/특수문자/겹치는 용어를 처리. `TranslateServiceTest`, `AwsTranslationGatewayTest`, `ApiContractTest` | 2023 연동은 B, 2026 구조·안정성 보강은 A |
| 번역 API 연동 | `POST /translate`; `TranslationGateway` port와 `AwsTranslationGateway` adapter | AWS SDK 기본 자격 증명 체인, region/timeout/retry 설정. AWS 4xx 일부→400, 인증·권한·429·5xx/client 오류→503 | 2023 B, 2026 A |
| 요청 검증 | 각 request record의 Bean Validation, `OnCreate` 그룹 | 필드별 `ValidationApiError` 400; `ApiContractTest` | 2026 A (`f001bb3`) |
| 계정 | `AccountController` → `AccountService` → `AccountRepository` → `Account/DTO` | 소유권 검사, 404, 중복 생성의 경쟁 상황 처리, 무결성 충돌 409; service/mapping/API 테스트 | 원형 B/D, 2026 개선 A |
| 회의록 | `MeetingController` → `MeetingService` → `MeetingRepository` → `Meeting/DTO` | 계정과 소유권 확인, CRUD, 404/403/검증; API·JPA 통합 테스트 | 원형 팀 구현, 2026 계층/검증 개선 A |
| 일정 | `ScheduleController` → `ScheduleService` → `ScheduleRepository` → `Schedule/DTO` | 계정·소유권, 날짜 조회, CRUD; API·JPA 통합 테스트 | 원형은 `sooobb` 커밋, 2026 개선 A |
| 인증·인가 | MVC interceptor와 `FirebaseTokenVerifier`, `OwnershipGuard` | Firebase Admin SDK; 토큰 누락/불량 401, 타 사용자 403; interceptor/API 테스트 | 2026 A (`31ca0c3`, `c0ea737`) |
| 공통 오류 | `GlobalExceptionHandler`, `ApiError`, `ValidationApiError` | 400/401/403/404/409/503 구조화 응답; API 계약 테스트 | 2026 A (`cf88b17` 등) |

즐겨찾기·북마크·알림의 Controller/Service/Repository/Entity/DTO/테스트는 발견되지 않았다.

## 5. AWS 실행 환경 구성 근거

확인 가능한 AWS 서비스는 **Amazon EC2**와 **Amazon Translate(Custom Terminology 포함)**뿐이다.

- 2023년 README 역할 기록은 본인이 EC2 인스턴스, 보안 그룹·포트, 실행 환경을 구성하고 SSH로 Spring Boot 백엔드를 배포·관리했다고 명시한다.
- 현재 백엔드는 Java 17 애플리케이션이며, DB 연결은 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DDL_AUTO` 환경변수로 외부화되어 있다. Translate region과 timeout/retry도 환경변수로 조절한다.
- AWS 자격 증명은 코드나 저장소 파일에 저장하지 않고 SDK의 `DefaultAWSCredentialsProviderChain`을 사용한다.
- H2가 현재 저장소의 백엔드 DB다. 별도 RDS 사용 근거는 없다. Firebase/Firestore는 모바일 데이터·인증 경계이며 EC2의 관계형 DB로 표현하면 안 된다.
- Dockerfile, Compose, Terraform, CloudFormation, CodeDeploy, ECS/EKS/Lambda, Nginx, systemd unit, 배포 shell, EC2 AMI/instance type/IP/보안 그룹 규칙, 모니터링 자료는 없다.

따라서 안전한 표현은 “프로젝트 당시 EC2에 Spring Boot 실행 환경을 구성하고 SSH 기반 배포·연결을 담당했다”이다. “AWS 운영 환경 전체 설계”, “고가용성/무중단 배포”, “현재 운영”, “RDS·S3·Route 53 등을 사용”은 증거가 없다.

## 6. 확인된 문제해결 사례

### 사례 1 — 전문용어가 없는 일반 문장에서 발생하던 오류

1. **문제 상황:** 2023 번역 코드는 AWS 응답의 `AppliedTerminologies[0].Terms[0]`에 바로 접근해, 적용 용어가 없는 일반 문장에서 실패할 수 있었다.
2. **원인:** 빈 적용 용어 목록을 고려하지 않은 인덱스 접근이었다.
3. **본인 역할:** 2023 수정 커밋 작성자는 `sooobb`이므로 본인 단독 해결로 볼 수 없다. 팀 번역 기능 공동 참여 범위의 사례다.
4. **해결:** `557be7f`에서 목록 크기를 확인하고, 비어 있으면 최초 번역문을 그대로 반환했다.
5. **후속 개선:** 2026년 본인이 gateway에서 null 목록을 빈 리스트로 정규화하고, service에서 사용 가능한 용어가 없으면 즉시 최초 결과를 반환하도록 계층화했다.
6. **검증:** 현재 `TranslateServiceTest`와 `AwsTranslationGatewayTest`가 빈/null/불완전 용어 응답을 검증한다.
7. **결과:** 용어가 없는 번역과 비정상 용어 메타데이터가 안전하게 처리된다. 실제 AWS 품질 향상률은 측정하지 않았다.

### 사례 2 — 단일 용어·문자열 인덱스 중심 후처리의 취약성

1. **문제 상황:** 2023 구현은 첫 용어만 처리하고 `substring`의 위치 보정에 의존했다. 여러 용어, 겹치는 용어, `$`·역슬래시 같은 치환 특수문자에서 취약했다.
2. **원인:** 외부 API 응답 구조와 문자열 치환을 Controller가 동시에 담당했고, 첫 결과만 가정했다.
3. **본인 역할:** 2026년 개인 유지보수에서 번역 port/adapter/service로 분리하고 후처리를 일반화했다.
4. **해결:** 유효 용어만 필터링하고 긴 source부터 정렬하며 `Pattern.quote`/`Matcher.quoteReplacement`로 모든 용어를 표시한 뒤, 두 번째 번역문의 target 표식을 제거한다.
5. **검증:** 복수·겹침·중복·특수문자·빈 target·원문에 없는 용어 등을 `TranslateServiceTest`에서 mock gateway로 검증한다.
6. **결과:** 조건별 로직이 재현 가능한 단위 테스트를 갖게 됐다. 실서비스 정확도 개선 수치는 없다.
7. **코드/커밋:** `TranslateService.java`, `TranslateServiceTest.java`; `ccc06b0`, `f029bd0`, `f8bd7a5`, `763186f`.

### 사례 3 — 외부 번역 실패와 모바일 저장 일관성

1. **문제 상황:** AWS 장애/잘못된 요청과 빈 응답·timeout이 사용자에게 일관되게 전달되지 않거나, 번역 실패 메시지가 채팅에 저장될 위험이 있었다.
2. **원인:** provider 예외 경계, 클라이언트 timeout, 성공 이후 저장 순서가 명시되지 않았다.
3. **본인 역할:** 2026년 백엔드 예외 매핑과 모바일 API/화면 테스트를 보강했다.
4. **해결:** AWS 예외를 요청 오류(400)와 provider 장애(503)로 나누고, 모바일에 10초 timeout·빈 문자열 거부·재시도 UI를 추가했다. 채팅/Firestore 저장은 번역 성공 callback 안에서만 수행한다.
5. **검증:** `AwsTranslationGatewayTest`, `ApiContractTest`, `translationApi-test.js`, `translationScreen-test.js`가 오류 매핑, timeout, 빈 응답, 재시도, 성공 후 저장을 검증한다.
6. **결과:** 실패 상태가 사용자 메시지로 저장되지 않고 재시도할 수 있다. 실제 장애율 감소 수치는 없다.
7. **커밋:** `26af082`, `b7b4842`, `763186f`.

## 7. 테스트 및 검증 결과

### 2026-10-14 신규 실행

| 검사 | 결과 | 범위/제한 |
| --- | ---: | --- |
| `backend/gradlew.bat clean test` (Java 17) | **56/56 통과**, Gradle build successful | mock AWS/Firebase 및 H2 중심. 실 AWS/Firebase 호출 없음 |
| `npm.cmd test -- --runInBand` | **6/6 suites, 16/16 tests 통과** | Jest mock 중심. native/E2E 아님 |
| `npm.cmd run lint` | **통과** | ESLint 정적 검사 |

현재 루트 README의 56 backend, 6 suites/16 mobile, ESLint 통과 기록과 일치한다. `backend/README.md`의 “2026-08-10 기준 55개”는 당시 기록이며 현재보다 1개 적다. 보관 README의 55 backend/9 mobile도 과거 시점 기록이다.

GitHub Actions는 push/PR마다 Ubuntu에서 JDK 17의 backend tests와 Node 20의 mobile tests/ESLint를 실행한다. 2026-10-18 감사에서 로컬 테스트·lint·backend build를 다시 실행했다. Android `assembleDebug`도 재실행했으나 STT 의존성의 Kotlin Gradle plugin 1.3.50과 AGP 최소 1.5.20 충돌로 실패했다. iOS build, STT, 실 Firebase, 실 Amazon Translate, Kakao KoGPT, EC2 배포는 실행하지 않았다. Mobile `npm audit --omit=dev`는 75건(critical 4, high 36, moderate 30, low 5)으로 실패했고, 비강제 `npm audit fix`도 React Native peer dependency 충돌로 적용되지 않았다.

## 8. 당시 구현과 이후 개선 구분

| 기간 | 확인된 범위 |
| --- | --- |
| 2023-03~06 팀 프로젝트 | 모바일 앱, Spring/JPA CRUD 원형, Amazon Translate/Custom Terminology 연동, 일정·회의록 기능. 데이터셋 및 EC2는 역할 문서상 본인 주도, 번역 기능은 공동 구현. 연결된 원본 백엔드 코드 커밋은 주로 `sooobb`, merge는 `Hwang SooYeon`이다. |
| 2026-07~09 개인 유지보수 | 원본 이력 연결, 패키지 정리, Controller-Service-Repository 분리, DTO/validation, Firebase 인증·소유권, 공통 오류, Flyway, 번역 gateway/장애 처리, 환경변수, 모바일 API 분리, 테스트·CI·lint·문서 보강. 본인 커밋으로 직접 확인된다. |

2026년 변경은 과거 팀 구현의 품질을 개선한 개인 성과로 말할 수 있지만, 이를 2023년 당시 구현으로 표현하면 안 된다. 반대로 2023년 데이터셋/EC2 경험은 역할 기록은 있으나 2026년 코드 커밋으로 증명되는 항목이 아니다.

## 9. Java/Spring·기업 IT 직무 활용 가능성

- **Java/Spring API:** REST endpoint, 계층 분리, DTO/Bean Validation, 공통 오류 계약, Firebase 인증·리소스 소유권 검사를 현재 코드와 테스트로 설명할 수 있다.
- **외부 API 통합:** AWS SDK를 adapter 뒤로 격리하고, 4xx/5xx/client failure를 도메인 예외 및 HTTP 응답으로 변환하며 timeout/retry를 설정한 경험이 확인된다.
- **데이터 처리:** source-target 전문용어를 분야별 용어집으로 구성하고 번역 파이프라인에 연결한 경험은 활용 가능하다. 다만 원본과 전체 규모가 없어 “대규모 데이터 파이프라인”으로 표현할 수 없다.
- **DB/기업 업무 API:** JPA 관계, repository 조회, Flyway migration, 무결성 충돌, CRUD 통합 테스트 경험을 코드로 제시할 수 있다.
- **클라우드:** EC2 실행 환경과 Translate 연동 경험을 구분해 설명할 수 있다. 운영 플랫폼 전체 설계나 SRE 경험으로 확장하면 안 된다.
- **레거시 개선:** 2023 원형을 보존하면서 2026년에 테스트 가능한 경계와 CI를 만든 사례가 가장 강한 개인 코드 근거다.

권장 표현 예시: “3인 팀의 통번역 서비스에서 분야별 전문용어 데이터셋과 EC2 실행 환경 구성을 주도하고 Amazon Translate 연동에 공동 참여했다. 이후 개인 유지보수에서 Spring API 계층화, 인증·인가, Flyway, 외부 API 장애 처리와 자동 테스트를 보강했다.”

## 10. 미확인 사항 및 표현 금지 항목

- 원본 데이터셋, 전체 행 수, 수집 출처·라이선스, 중복/누락/정규화 상세, 업로드 이력은 미확인이다.
- 정량적 번역 정확도·품질·처리속도·비용·사용자 수 개선 근거가 없다.
- 번역 모델을 직접 개발·학습했다고 표현할 수 없다. AWS Translate Custom Terminology를 구성·연동했다.
- EC2 instance type, OS, 보안 그룹 규칙, 프로세스 관리자, 도메인/TLS, 배포 자동화, 운영 기간·가용성은 미확인이다.
- RDS, S3, ECS, EKS, Lambda, API Gateway, CloudFront, Route 53, CloudWatch 등을 사용했다는 근거가 없다.
- 현재 서비스가 운영 중이거나 배포 환경이 살아 있다고 표현할 수 없다.
- Spring Boot 전체, React Native 전체, 번역 기능 전체를 본인 단독 구현으로 표현할 수 없다.
- 북마크·즐겨찾기·알림 기능은 저장소에 없다.
- 실 AWS/Firebase/Kakao, Android/iOS native, E2E 품질은 이번 로컬 테스트가 보증하지 않는다.
- `git shortlog`의 커밋 수는 생산성·기여율 지표가 아니다.

추가 검증에는 2023 데이터 원본/출처표, AWS Custom Terminology import 기록, EC2 콘솔 캡처·배포 메모·실행 로그, 발표자료/업무분장표, 당시 팀원 확인이 필요하다.

## 11. 관련 파일과 커밋 링크

### 파일

- [루트 README](../README.md)
- [2023 역할 기록 보관본](archive/README-2023-original.md)
- [데이터셋 공개 예시](images/readme/dataset_example.png)
- [번역 Controller](../backend/src/main/java/com/site/transmate/translation/TranslateController.java)
- [전문용어 후처리 Service](../backend/src/main/java/com/site/transmate/translation/TranslateService.java)
- [AWS Translate adapter](../backend/src/main/java/com/site/transmate/translation/aws/AwsTranslationGateway.java)
- [공통 오류 처리](../backend/src/main/java/com/site/transmate/api/GlobalExceptionHandler.java)
- [DB 초기 schema](../backend/src/main/resources/db/migration/V1__create_initial_schema.sql)
- [모바일 번역 API](../mobile/lib/translationApi.js)
- [CI workflow](../.github/workflows/ci.yml)

### 핵심 커밋

- 2023 공동 구현: [`08ee938` 번역 기능 추가](https://github.com/minjuko/2023capstone/commit/08ee9383195794bafa5c7407b8d08c2668acf0f0), [`557be7f` 일반 문장 번역 오류 해결](https://github.com/minjuko/2023capstone/commit/557be7f189e3ccdd9fe8920b9c2896dfa40ca641)
- 이력 경계: [`f0acd3b` 원본 팀 이력 연결](https://github.com/minjuko/transmate/commit/f0acd3b), [`0047465` 팀/포트폴리오 이력 구분](https://github.com/minjuko/transmate/commit/0047465)
- 2026 Spring/번역: [`ccc06b0` 번역 API 분리](https://github.com/minjuko/transmate/commit/ccc06b0), [`f001bb3` DTO 검증](https://github.com/minjuko/transmate/commit/f001bb3), [`26af082` 번역 실패 응답](https://github.com/minjuko/transmate/commit/26af082), [`b7b4842` 번역 운영 안정성](https://github.com/minjuko/transmate/commit/b7b4842)
- 2026 인증/DB/품질: [`c0ea737` Firebase 인증 개선](https://github.com/minjuko/transmate/commit/c0ea737), [`e537719` Flyway 개선](https://github.com/minjuko/transmate/commit/e537719), [`4d9270b` CI](https://github.com/minjuko/transmate/commit/4d9270b), [`763186f` 오류 처리 테스트 보강](https://github.com/minjuko/transmate/commit/763186f)

