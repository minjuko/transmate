# Transmate Mobile

Transmate의 React Native 모바일 클라이언트입니다. 로그인부터 실시간 대화 번역,
회의록·요약·PDF 저장, 일정 관리까지 모바일 화면을 담당합니다. 전체 서비스 기능과
화면 구성은 [루트 README](../README.md)에서 확인할 수 있습니다.

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

2026-09-18 기준 Jest 6개 스위트·16개 테스트와 ESLint가 통과합니다. 번역 요청의 10초 제한, 빈 응답 처리, 실패 후 재시도와 성공 전 채팅 저장 방지를 모의 응답으로 검증했습니다. 실제 번역 API와 음성 인식은 검증하지 않았습니다.

## Android native build 제한

`cd android`에서 Windows 기준 `.\gradlew.bat assembleDebug`를 실행하면 현재 STT 의존성 `react-native-google-cloud-speech-to-text` 0.5.4가 선언한 Kotlin Gradle plugin 1.3.50 때문에 빌드가 중단됩니다. 앱은 Kotlin 1.5.30·Android Gradle Plugin 7.3.1을 사용하며, 오류 메시지는 Android Gradle Plugin에 Kotlin Gradle plugin 1.5.20 이상이 필요하다고 명시합니다. 이번 검증에서는 native 의존성을 변경하지 않았습니다. iOS native build와 STT 기능도 검증하지 않았습니다.
