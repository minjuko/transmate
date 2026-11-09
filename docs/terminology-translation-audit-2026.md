# TransMate 전문용어 번역 기능 기술 감사

검토 기준일: 2026-11-09
검토 대상: 현재 `main` 소스와 Git 이력, 기존 테스트, GitHub Actions 공개 실행 결과

## 요약

현재 구현은 모바일이 번역 요청에 전문용어집 이름을 포함하고, Spring Boot가 AWS Translate `TranslateText` 요청에 그 이름을 전달하는 구조다. AWS는 등록된 사용자 지정 용어집을 적용하고 적용 항목을 응답에 돌려주며, 애플리케이션은 그 응답을 이용해 용어 경계를 표시한 두 번째 번역을 요청한 뒤 표시용 대괄호를 제거한다. 저장소에는 용어집 생성·업로드·목록·상태 조회 코드가 없다.

실제 AWS 자격 증명으로 번역을 수행하지 않았다. 따라서 이 문서와 mock 테스트는 API 계약 전달, 응답 변환, 예외 매핑만 입증하며 번역 품질이나 용어 일치율 향상을 입증하지 않는다.

## 실행 구조와 기술 스택

- **Mobile:** React Native 0.71.8, React 18.2, JavaScript. `ChattingScreen`이 입력/음성 인식 결과의 언어쌍을 정하고 `mobile/lib/translationApi.js`가 Axios로 백엔드를 호출한다. Firebase Auth 토큰은 공통 Axios 요청 인터셉터가 붙인다.
- **Backend:** Java 17, Spring Boot 3.0.6, Spring MVC/Validation, Spring Data JPA, Flyway, H2. 번역은 Controller → Service → `TranslationGateway` → AWS SDK for Java v1 `AmazonTranslate` 순서다.
- **저장 데이터:** Account, Meeting, Schedule은 JPA/Flyway로 관리한다. 번역 본문이나 AWS 용어집은 로컬 DB에 저장하지 않는다. 채팅/일부 사용자 데이터는 모바일에서 Firebase 서비스를 직접 사용한다.
- **인증:** Firebase ID token 검증 인터셉터가 `/translate`에 적용된다. 번역 endpoint는 인증 보호 대상이며, 현재 번역 요청에는 별도 사용자 소유 데이터가 없다.

## 요청·응답 계약

모바일은 `POST /translate`에 다음 JSON을 보내고, 응답은 JSON 객체가 아닌 번역 문자열이다.

```json
{
  "Text": "source text",
  "TerminologyNames": "PORT",
  "SourceLanguageCode": "en",
  "TargetLanguageCode": "ko"
}
```

`TranslateRequest`는 본문/언어 코드를 필수 검증하고 언어 코드 형식은 소문자 2~3자로 제한한다. `ChattingScreen`은 음성인식용 locale(예: `en-US`)에서 첫 두 글자를 사용해 번역 코드(예: `en`)로 보낸다. 직접 번역과 기록 재번역 모두 동일 API helper를 쓴다. 빈 응답은 모바일에서 오류로 바뀌며, 10초를 넘긴 요청은 재시도 안내 대상이다.

## AWS 사용자 지정 용어집 적용

1. 분야 선택값은 `TerminologyNames`로 백엔드 DTO에 들어간다. `default` 선택은 모바일에서 빈 문자열로 바뀐다.
2. AWS gateway는 이름이 비어 있지 않을 때만 `TranslateTextRequest.withTerminologyNames(...)`를 설정한다. 이름이 비면 AWS 요청에서 해당 필드를 생략한다.
3. gateway는 AWS 응답의 `AppliedTerminologies[].Terms[]`에서 source/target 쌍을 추출한다.
4. service는 반환된 적용 용어를 길이 내림차순으로 정렬하고 원문에서 더 긴 겹침을 먼저 대괄호로 표시한다. 표시한 원문을 같은 언어쌍·용어집 이름으로 다시 번역한 뒤 번역문에서 `[target term]`을 제거한다. 적용 용어가 없거나 원문에서 찾을 수 없으면 첫 번역 결과를 반환한다.

이는 용어집 관리 기능이 아니라 AWS에 사전 등록되어 사용 가능한 용어집을 이름으로 참조하는 통합이다. 등록 주체, CSV 업로드, 언어쌍/용어집 상태 조회, 활성 여부 확인은 이 저장소 구현 범위에 없다. 이름이 잘못되었거나 사용할 수 없는 경우 AWS 오류를 받아 API 오류로 변환한다.

## 실패·시간 제한 처리

- AWS 4xx 중 401, 403, 429를 제외한 상태는 요청 오류로 보고 HTTP 400으로 매핑한다. 401/403/429, AWS 5xx, SDK client/network 예외는 공급자 오류로 보고 HTTP 503으로 매핑한다. 외부 SDK 상세 메시지는 응답 메시지에 노출하지 않는다.
- AWS client 기본값은 connection timeout 3초, socket timeout 10초, 최대 재시도 2회(`maxErrorRetry`)다. 배포 환경 변수로 덮어쓸 수 있다. 재시도 횟수와 각 연결/소켓 대기 시간이 있으므로 한 번의 SDK 호출도 모바일 제한보다 오래 걸릴 수 있다.
- 모바일 제한은 10초다. 용어가 적용되면 첫 번역과 표시한 원문의 번역이 순차 실행되어 gateway 호출이 최대 2회 발생한다. 따라서 실패/지연 상황의 총 서버 처리 시간이 모바일 제한을 넘을 수 있는 **시간 예산 불일치 위험**이 있다. 모바일 10초 초과는 실제 지연으로 재현하지 않았고 운영 지표로 확인하지 않았으므로 관측된 장애로 표현하지 않는다.
- `TranslateRequest`의 정규식은 코드 모양만 검증한다. AWS가 지원하지 않는 소문자 2~3자 값은 형식 검증을 통과한 뒤 AWS에서 거부될 수 있으며, gateway 오류 매핑 테스트가 그 계약을 다룬다.

## 검증 시나리오와 근거

실제 AWS 호출이 없어 AWS client mock으로 확인 가능한 범위만 검증한다. 동일한 원문/언어쌍에서 용어집 이름이 있을 때 요청에 이름이 포함되고, 이름이 공백이면 생략되는 계약은 `AwsTranslationGatewayTest`로 확인한다. 결과 문자열은 mock이 정한 값이므로 자연스러움이나 정확도 비교로 해석하면 안 된다.

| 시나리오 | 검증 근거 | 판정 범위 |
| --- | --- | --- |
| 용어집 이름 제공 / 미제공 | `AwsTranslationGatewayTest`의 요청 캡처·빈 이름 생략 | AWS 요청 필드 계약 |
| AWS가 적용 용어를 반환 | mock 응답의 source/target term 매핑 | response DTO 변환 |
| 미적용/원문에 없는 표현 | `TranslateServiceTest`에서 적용 용어 없음, 원문 불일치, 빈 source/target 처리 | 1회 결과 반환, 불필요한 2차 호출 없음 |
| 여러 용어 및 겹치는 용어 | `TranslateServiceTest`에서 긴 용어 우선 표시, 다중 치환 | 서로 다른 겹침 용어의 요청 조립/marker 제거. 동일 용어 항목이 중복 반환되는 별도 테스트는 없음 |
| 특수문자 용어 | `C++`, `node.js` mock 사례 | 정규식 메타문자를 문자 그대로 처리 |
| 잘못된 언어쌍 | AWS `UnsupportedLanguagePairException` 400 mock | HTTP 요청 오류 매핑 |
| 잘못되었거나 사용 불가한 용어집 | AWS `InvalidParameterValueException` 400 mock | 안전한 HTTP 400 매핑. AWS 실제 상태/메시지 확인은 아님 |
| 인증·권한·rate limit, 5xx, client/network failure | gateway mock 테스트 | 공급자 오류/HTTP 503 매핑 및 상세 미노출 |
| 모바일 빈 응답·타임아웃 | `translationApi-test.js` | 빈 문자열 거부, 10초 timeout 오류 안내 |

## 재현한 결함과 수정

이번 검토에서 기존 테스트에 없는, 실제 동작으로 재현 가능한 기능 결함을 확인하지 못했다. 이미 존재하던 gateway/service/mobile 테스트가 위 계약 시나리오 상당수를 다루고 있어 제품 코드 수정이나 중복 회귀 테스트 추가는 하지 않았다. 시간 예산 불일치는 코드 설정에서 확인한 위험으로 기록했다. 지연 재현, 서버/모바일 timeout 동시 조정은 이 검토에서 실행하지 않았다.

## 역할 귀속

- **2023 팀 프로젝트:** 당시 역할 기록 기준으로 전문용어 데이터셋 구축과 AWS EC2 실행 환경 구성을 주도했고, Spring Boot API 및 AWS Translate 사용자 지정 용어집 연동은 공동 참여했다. 현 저장소의 과거 Git 이력도 용어집 연동 공동 구현을 뒷받침하지만, 원본 데이터셋과 당시 EC2 실행 로그는 현재 저장소에서 재검증할 수 없다. 따라서 이 역할 구분은 당시 기록/기여 감사에 근거하며, 전체 API나 연동을 단독 구현했다고 주장하지 않는다.
- **2026 개인 후속 작업:** 현재 Git 이력에는 본인 계정의 번역 API gateway 구조 분리, 입력 검증, 외부 실패·빈 응답 처리, timeout/retry 설정, AWS client mock 계약 테스트 보강 커밋이 있다. 이번 전문용어 번역 동작 감사와 문서화도 현재 소스·테스트를 다시 대조한 개인 검증 작업이다.
- 이번 감사의 변경물은 이 문서 하나다. README와 Notion은 수정하지 않았다.

## 테스트·빌드·CI 확인 기록

문서 검수 후 로컬에서 실행한 결과와 조회한 원격 CI 결과다.

| 확인 | 결과 |
| --- | --- |
| Backend clean test/build | `backend/gradlew.bat clean build` — **성공**, 56 tests / 10 suites, 실패 0, skip 0 |
| Mobile Jest | `npm.cmd test -- --runInBand` — **성공**, 6 suites / 16 tests |
| Mobile ESLint | `npm.cmd run lint` — **성공** |
| GitHub Actions | **성공** — [`4293f9c` CI run #54](https://github.com/minjuko/transmate/actions/runs/42633206913), 2026-11-09, Backend와 Mobile job 모두 성공. 아래 최종 문서 수정 전 커밋 기준 |
| 실제 AWS Translate 호출 | 실행하지 않음 — 자격 증명/실번역 확인 없음 |
| Android/iOS native build 및 E2E | 실행하지 않음 |

## 포트폴리오용 요약

팀 프로젝트에서 AWS Translate 사용자 지정 용어집 연동에 공동으로 참여했고, 후속 개인 작업으로 Spring 번역 API를 gateway 구조로 분리해 입력 검증, 외부 오류 매핑, timeout/retry 설정과 mock 계약 테스트를 보강했다. 현재 검증은 AWS 요청 구성과 예외 처리까지이며 실제 번역 품질 향상은 주장하지 않는다.

## 변경되지 않은 한계

- AWS 실호출이 없어 실제 등록 용어집의 상태, 언어쌍 호환성, 적용 결과, 비용과 지연은 검증되지 않았다.
- 용어집 업로드/조회/상태 확인 기능은 현재 코드에 없다.
- Android/iOS native build 및 앱 종단 간 시나리오는 이번 검증에 포함하지 않는다.
- 새 문서 커밋을 push한 뒤 발생한 CI는 최종 보고에서 별도로 확인한다.
