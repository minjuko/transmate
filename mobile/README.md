# Transmate Mobile

Transmate의 React Native 모바일 클라이언트입니다. 로그인, 실시간 대화 번역,
회의록·요약·PDF 저장과 일정 관리 화면을 제공합니다. 서비스 기능과 화면은
[루트 README](../README.md)를 참고하세요.

## Stack

- Node.js 18
- React Native 0.71, React 18
- React Navigation
- Firebase Authentication, Firestore
- Google Cloud Speech-to-Text
- Jest, ESLint

## Requirements

- Node.js 18
- Android Studio 또는 Xcode
- Firebase 프로젝트 설정
- Android 실행 시 `android/app/google-services.json`
- iOS 실행 시 CocoaPods

## Environment

`.env.example`을 `.env`로 복사한 뒤 값을 설정합니다.

```dotenv
BACKEND_API_URL=http://localhost:8080
GOOGLE_TRANSLATE_API_KEY=
GOOGLE_SPEECH_API_KEY=
KAKAO_REST_API_KEY=
```

모바일 앱에 포함되는 API 키는 완전한 비밀로 유지할 수 없습니다. 각 공급자
콘솔에서 앱·API·사용량 제한을 설정해야 합니다.

Android 에뮬레이터에서 로컬 백엔드에 연결할 때는 환경에 따라
`http://10.0.2.2:8080`을 사용해야 할 수 있습니다.

## Install

```powershell
npm.cmd ci
```

iOS는 의존성 설치 후 CocoaPods 설정이 추가로 필요합니다.

```bash
bundle install
bundle exec pod install --project-directory=ios
```

## Run

Metro 실행:

```powershell
npm.cmd start
```

별도 터미널에서 앱 실행:

```powershell
npm.cmd run android
```

```bash
npm run ios
```

## Test

```powershell
npm.cmd test -- --runInBand
npm.cmd run lint
```

2026-07-30 기준 Jest 9개 테스트와 ESLint가 통과합니다.
