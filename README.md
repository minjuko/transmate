# Susukang
**비즈니스 회의를 위한 실시간 통번역 서비스**

일반 번역기는 전문 분야에 대한 단어나 약어가 모두 적용되어있지 않으며, 중복된 단어의 경우 의미가 정확히 전달되지 않을 수있다.</br>
기존의 자동 번역 기술에 더하여 비즈니스 언어 번역 문제를 해결할 수 있는 실시간 통번역 서비스를 구현하고자 하였다.

<h2>개발 파트</h2>
팀: 3명 - FE 1, BE 2

</br>
</br>

**Frontend**
- ReactNative

</br>

@H-sooyeon
- UI 설계
- 1:1 음성 및 텍스트 채팅
- 문서 저장 및 생성, 다운로드
- 캘린더 일정 관리
- 채팅 대화 내용 문서화

</br>

**Backend**
- SpringBoot
- H2 Database
- AWS

</br>

@soobb
- AWS translate DB 구축
- 데이터셋 제작

@minjuuko 
- AWS 서버 구축
- 데이터셋 제작

</br>

<h2>시스템 구조</h2>

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/31e5de14-e3de-4bc2-826d-b0e06a597c8c)

</br>

<h2>서버</h2>

**서버 통신**

서버와 클라이언트가 데이터를 주고받을 수 있도록 REST API로 구축하였다. 
1) 사용자 
- 사용자를 생성하고 사용자에 대한 정보를 전송한다.
2) 회의록
- 사용자 id를 받아 해당 사용자가 가지고 있는 모든 회의록에 대한 정보를 전송한다.
또는 사용자 id와 부분 문자열을 입력받아 회의록의 제목에 부분 문자열이 포함된 회의록만을 전송할 수 있다.
- 사용자 id와 함께 회의록 정보를 받아 회의록을 생성한다.
- 회의록 id를 입력받아 회의록을 수정하거나 삭제한다.
3) 일정
- 사용자 id를 받아 해당 사용자가 가지고 있는 모든 일정에 대한 정보를 전송한다.
- 사용자 id와 함께 일정 정보를 받아 일정을 생성한다.
- 일정 id를 입력받아 일정을 수정하거나 삭제한다.
4) 번역
- /translate 엔드포인트로 POST요청을 받아 번역 작업을 수행한다.
- 용어사전의 이름, source 언어코드, target 언어코드, 번역할 문장을 받아 AWS의 Amazon Translate 서비스를 사용하기 위한 클라이언트 객체를 생성한 후 AWS 자격 증명 정보와 지역을 설정한 후, 번역 요청을 처리하여 최종 번역 결과를 문자열로 반환하여 클라이언트에게 응답한다.

소스코드: https://github.com/sooobb/Susukang

</br>

<h2>실행 환경 설정</h2>

백엔드는 다음 환경변수를 실행 프로세스에 설정해야 한다.

| 환경변수 | 필수 | 기본값 | 설명 |
| --- | --- | --- | --- |
| `DB_URL` | 예 | 없음 | JDBC 데이터베이스 URL |
| `DB_USERNAME` | 예 | 없음 | 데이터베이스 사용자명 |
| `DB_PASSWORD` | 예 | 없음 | 데이터베이스 비밀번호 |
| `DB_DRIVER_CLASS_NAME` | 아니요 | `org.h2.Driver` | JDBC 드라이버 클래스 |
| `DB_DIALECT` | 아니요 | `org.hibernate.dialect.H2Dialect` | Hibernate dialect |
| `DB_DDL_AUTO` | 아니요 | `validate` | Hibernate 스키마 정책 |
| `H2_CONSOLE_ENABLED` | 아니요 | `false` | H2 콘솔 활성화 여부 |
| `H2_CONSOLE_ALLOW_OTHERS` | 아니요 | `false` | H2 콘솔 외부 접속 허용 여부 |
| `AWS_TRANSLATE_REGION` | 아니요 | `ap-northeast-2` | Amazon Translate 리전 |

Firebase Admin과 AWS SDK 자격 증명은 코드나 설정 파일에 저장하지 않고 각 SDK의 기본 자격 증명 체인으로 제공한다. 운영에서는 `DB_DDL_AUTO=validate`를 유지하고 H2 콘솔을 활성화하지 않는다.

PowerShell 실행 예시:

```powershell
$env:DB_URL="jdbc:h2:tcp://localhost/~/local"
$env:DB_USERNAME="ubuntu"
$env:DB_PASSWORD="비밀번호"
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\firebase-service-account.json"
Set-Location backend
.\gradlew.bat bootRun
```

모바일은 `mobile/.env.example`을 `mobile/.env`로 복사한 뒤 환경에 맞는 주소를 설정한다. 실제 `.env` 파일은 Git에 포함되지 않는다.

```dotenv
BACKEND_API_URL=http://localhost:8080
```

Android 또는 iOS 앱은 환경값이 네이티브 빌드에 포함되므로 `.env` 변경 후 앱을 다시 빌드해야 한다.

</br>

<h2>모바일</h2>

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/ce491d75-a3fb-45eb-bccb-08d5d40738e1)

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/0e90b3bb-b224-428e-ade9-15e93c6e45b9)

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/e9a4bd7c-543b-40fd-8024-3305eff737ec)

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/a604a901-01a1-4c05-84b9-42cf53e84a26)

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/8388ec8d-08bb-422e-9610-98383d51c698)

![image](https://github.com/H-sooyeon/Susukang/assets/56586470/d7b16d99-f1bd-4b44-b3a5-951ba7ec37ee)
