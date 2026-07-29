# Transmate

> 비즈니스 회의를 위한 전문 용어 기반 실시간 통번역 및 회의 관리 서비스

일반 번역에서 정확히 전달되기 어려운 분야별 용어와 약어를 반영하기 위해
Amazon Translate의 Custom Terminology를 적용했습니다. 사용자는 상대방과
음성·텍스트로 대화하고, 번역된 대화 내용을 회의록으로 저장해 검색·수정·
요약·PDF 다운로드할 수 있으며 일정도 함께 관리할 수 있습니다.

## 프로젝트 개요

| 항목 | 내용 |
| --- | --- |
| 기간 | 2023.03–2023.06 |
| 형태 | 캡스톤 디자인 팀 프로젝트 |
| 인원 | 3명: 모바일 1명, 백엔드 2명 |
| 플랫폼 | React Native 모바일 앱 |
| 핵심 기능 | 실시간 대화 번역, 전문 용어 적용, 회의록·일정 관리 |

## 시스템 구성

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/31e5de14-e3de-4bc2-826d-b0e06a597c8c" alt="Transmate 시스템 구성도" width="720">
</p>

- React Native 앱에서 Firebase Authentication으로 사용자를 인증합니다.
- Spring Boot REST API가 계정·회의록·일정 데이터를 관리합니다.
- Amazon Translate와 분야별 Custom Terminology로 대화를 번역합니다.
- 번역된 대화는 회의록으로 저장하고 Kakao KoGPT 기반 요약 기능과 연결했습니다.

## 주요 기능

### 1. 회원가입 및 로그인

이메일과 비밀번호로 계정을 생성하고 로그인합니다. 사용자 계정은 Firebase
Authentication으로 관리하며, 로그인한 사용자별로 회의록과 일정을 구분합니다.

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/ce491d75-a3fb-45eb-bccb-08d5d40738e1" alt="회원가입과 로그인 화면" width="900">
</p>

### 2. 회의록 관리

회의록을 생성·수정·삭제하고 제목과 세부 내용으로 검색할 수 있습니다.
저장된 문서는 PDF로 내려받거나 별도의 요약 문서로 만들 수 있습니다.

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/0e90b3bb-b224-428e-ade9-15e93c6e45b9" alt="회의록 생성 수정 삭제 화면" width="900">
</p>

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/e9a4bd7c-543b-40fd-8024-3305eff737ec" alt="회의록 검색과 PDF 다운로드 화면" width="900">
</p>

### 3. 일정 관리

캘린더에서 날짜를 선택해 회의 일정을 등록하고 시간과 내용을 수정하거나
삭제할 수 있습니다.

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/a604a901-01a1-4c05-84b9-42cf53e84a26" alt="캘린더 일정 관리 화면" width="900">
</p>

### 4. 실시간 대화 번역

대화 상대와 언어, 업무 분야를 선택한 뒤 1:1 채팅을 시작합니다. 음성 입력과
텍스트 입력을 지원하며, 선택한 분야의 Custom Terminology를 번역 요청에
적용해 전문 용어의 일관성을 높였습니다.

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/8388ec8d-08bb-422e-9610-98383d51c698" alt="대화 설정과 실시간 번역 채팅 화면" width="900">
</p>

### 5. 대화 저장 및 요약

번역 대화를 제목과 소속 정보와 함께 회의록으로 저장합니다. 저장한 대화는
문서 목록에서 다시 확인하고 핵심 내용만 요약할 수 있습니다.

<p align="center">
  <img src="https://github.com/H-sooyeon/Susukang/assets/56586470/d7b16d99-f1bd-4b44-b3a5-951ba7ec37ee" alt="번역 대화 저장과 회의록 요약 화면" width="900">
</p>

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Mobile | React Native, React Navigation |
| Authentication | Firebase Authentication |
| Realtime Data | Firebase Firestore |
| Backend | Java 17, Spring Boot, Spring Data JPA |
| Database | H2, Flyway |
| Translation | Amazon Translate, AWS Custom Terminology |
| Speech | Google Cloud Speech-to-Text |
| Summary | Kakao KoGPT API |
| Test / CI | JUnit 5, Mockito, MockMvc, Jest, ESLint, GitHub Actions |

## 팀 구성과 역할

| 담당 | 주요 역할 |
| --- | --- |
| Mobile | UI 설계, 1:1 음성·텍스트 채팅, 문서·일정 관리, 대화 문서화 |
| Backend | 계정·회의록·일정 REST API, Amazon Translate 연동 |
| 데이터·인프라 | 분야별 용어 데이터 제작, AWS 서버 구성과 백엔드 배포 |

### 담당 기여

팀 프로젝트에서 다음 영역을 맡았습니다.

- 모바일 앱이 접근할 수 있는 AWS 서버 환경 구축
- Spring Boot 백엔드 빌드 및 배포
- 분야별 전문 용어 데이터셋과 Custom Terminology 제작 참여
- Amazon Translate 연동과 Translate API 구현 참여

## 백엔드 API

| 도메인 | 기능 |
| --- | --- |
| Account | 사용자 생성 및 정보 조회 |
| Meeting | 회의록 생성·조회·검색·수정·삭제 |
| Schedule | 일정 생성·조회·검색·수정·삭제 |
| Translate | 원문·언어·용어집을 전달받아 번역 결과 반환 |

Firebase ID 토큰으로 사용자를 인증하고, 검증된 UID와 데이터 소유자를
비교해 다른 사용자의 회의록과 일정에 접근하지 못하도록 처리합니다.

## 포트폴리오 개선

팀 프로젝트의 기능과 화면은 유지하면서 백엔드 중심으로 다음 항목을
보완했습니다.

- 번역 로직을 Controller–Service–Gateway–AWS adapter로 분리
- 모바일과 백엔드의 요청·응답 계약 및 입력 검증 정비
- Firebase 인증과 사용자 데이터 소유권 검증
- AWS 자격 증명·리전·timeout·retry 설정 외부화
- AWS 오류를 안전한 `400`·`503` 응답으로 변환
- Flyway 기반 스키마 변경 관리와 JPA 통합 테스트
- 백엔드·모바일 테스트 및 GitHub Actions CI 구성
- 모바일은 사용자 흐름을 변경하지 않고, API 연동과 명백한 오류를 해결하는 최소 범위만 수정

## 검증

- 백엔드 테스트 49개 통과
- 모바일 Jest 테스트 9개 통과
- 모바일 ESLint 통과
- H2·Flyway 환경에서 Spring Boot 서버 기동 확인
- Amazon Translate 리소스는 현재 종료되어 실제 번역 성공 호출은 재검증하지 않고, 번역 로직과 AWS adapter는 mock 기반 테스트로 검증