# 🔗 UniMeet 프론트엔드 API 연동 가이드

## 📡 서버 정보

### Base URL
```
http://localhost:8080
```

### CORS 설정
- 허용된 Origin: `http://localhost:3000`, `http://localhost:19006`
- 허용된 메서드: GET, POST, PUT, DELETE, OPTIONS
- 허용된 헤더: Authorization, Content-Type

---

## 🔐 인증 시스템

### 1. 회원가입
**Endpoint:** `POST /auth/signup`

**Request Body:**
```json
{
  "email": "student@kku.ac.kr",        // 필수: 건국대 이메일만 허용
  "password": "password123",           // 필수: 최소 8자
  "name": "김건국",                    // 필수
  "nickname": "건국이",                // 필수
  "studentId": "202020945",           // 필수: 학번
  "department": "컴퓨터공학과",        // 필수
  "birth": "2001-03-15",              // 필수: YYYY-MM-DD
  "phone": "010-1234-5678",           // 필수
  "gender": "남",                     // 선택: "남" | "여"
  "mbti": "ENFP",                     // 선택: 온보딩에서 입력
  "interests": ["독서", "프로그래밍"], // 선택: 온보딩에서 입력
  "height": "175",                    // 선택
  "prefer": "켜짐",                   // 선택: "켜짐" | "꺼짐"
  "nonPrefer": "꺼짐"                 // 선택: "켜짐" | "꺼짐"
}
```

**Response:** `200 OK`
```json
"회원가입이 완료되었습니다. 이메일 인증을 진행해주세요"
```

**Error:** `400 Bad Request`
```json
{
  "timestamp": "2025-10-13T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "이미 존재하는 이메일입니다"
}
```

---

### 2. 이메일 인증 코드 발송
**Endpoint:** `POST /auth/send-verification`

**Request Body:**
```json
{
  "email": "student@kku.ac.kr"
}
```

**Response:** `200 OK`
```json
{
  "message": "인증 코드가 발송되었습니다",
  "email": "student@kku.ac.kr",
  "expiryMinutes": 10
}
```

**참고:**
- 6자리 숫자 코드 생성
- 10분 후 자동 만료
- 개발 환경에서는 콘솔에 코드 출력

---

### 3. 이메일 인증 확인
**Endpoint:** `POST /auth/verify-email`

**Request Body:**
```json
{
  "email": "student@kku.ac.kr",
  "code": "123456"
}
```

**Response:** `200 OK`
```json
{
  "verified": true,
  "message": "이메일 인증이 완료되었습니다",
  "email": "student@kku.ac.kr"
}
```

**Error:** `400 Bad Request`
```json
"잘못된 인증 코드이거나 만료된 코드입니다"
```

---

### 4. 로그인
**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "email": "student@kku.ac.kr",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzdHVkZW50QGtrdS5hYy5rciIsImlhdCI6MTcwNjg2...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

**사용법:**
```javascript
// 이후 모든 API 요청에 헤더 포함
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

---

## 👤 사용자 프로필

### 5. 내 프로필 조회
**Endpoint:** `GET /api/user/profile`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "id": "user123",
  "email": "student@kku.ac.kr",
  "name": "김건국",
  "nickname": "건국이",
  "studentId": "202020945",
  "department": "컴퓨터공학과",
  "birth": "2001-03-15",
  "phone": "010-1234-5678",
  "gender": "남",
  "mbti": "ENFP",
  "interests": ["독서", "프로그래밍", "영화감상"],
  "height": "175",
  "prefer": "켜짐",
  "nonPrefer": "꺼짐",
  "role": "USER",
  "isVerified": true,
  "createdAt": "2025-10-13T10:00:00",
  "updatedAt": "2025-10-13T10:00:00"
}
```

---

### 6. 프로필 업데이트
**Endpoint:** `PUT /api/user/profile`

**Headers:**
```
Authorization: Bearer {token}
```

**Request Body:** (수정할 필드만 포함)
```json
{
  "nickname": "새닉네임",
  "mbti": "INTJ",
  "interests": ["운동", "음악", "게임"],
  "height": "180",
  "prefer": "켜짐"
}
```

**Response:** `200 OK`
```json
{
  "id": "user123",
  "nickname": "새닉네임",
  "mbti": "INTJ",
  // ... 전체 프로필 정보
}
```

---

## 🤖 AI 매칭 시스템

### 7. AI 매칭 요청
**Endpoint:** `POST /api/matches/find`

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
```
limit: number (기본값: 10) - 반환할 최대 매칭 수
```

**Response:** `200 OK`
```json
{
  "message": "AI 매칭 완료",
  "requestUser": {
    "name": "김건국",
    "mbti": "ENFP",
    "department": "컴퓨터공학과",
    "interests": ["독서", "프로그래밍"]
  },
  "totalCandidates": 15,
  "matches": [
    {
      "userId": "user456",
      "name": "박대학",
      "nickname": "대학이",
      "department": "경영학과",
      "studentId": "202020946",
      "gender": "여",
      "age": 24,
      "mbti": "INTJ",
      "interests": ["독서", "영화감상", "여행"],
      "height": "165",
      "prefer": "켜짐",
      "nonPrefer": "꺼짐",
      "compatibilityScore": 0.87,
      "detailedScores": {
        "mbti": 0.9,
        "interests": 0.8,
        "personality": 0.7,
        "department": 0.4,
        "age": 0.9,
        "height": 0.8,
        "total": 0.87
      },
      "commonInterests": ["독서"]
    }
  ]
}
```

**필드 설명:**
- `compatibilityScore`: 0~1 사이의 종합 호환성 점수 (0.87 = 87%)
- `age`: 자동 계산된 나이 (한국식)
- `commonInterests`: 공통 관심사 배열
- `detailedScores`: 각 항목별 세부 점수

---

### 8. 특정 사용자와 호환성 조회
**Endpoint:** `GET /api/matches/compatibility/{targetUserId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "currentUser": {
    "id": "user123",
    "name": "김건국",
    "department": "컴퓨터공학과"
  },
  "targetUser": {
    "id": "user456",
    "name": "박대학",
    "department": "경영학과"
  },
  "compatibilityScores": {
    "mbti": 0.9,
    "interests": 0.8,
    "personality": 0.7,
    "department": 0.4,
    "age": 0.9,
    "height": 0.8,
    "total": 0.87
  }
}
```

---

## 💬 채팅 시스템 (WebSocket)

### 9. WebSocket 연결
**Endpoint:** `ws://localhost:8080/chat`

**연결 방법:**
```javascript
const socket = new WebSocket('ws://localhost:8080/chat');

// 또는 SockJS 사용
const socket = new SockJS('http://localhost:8080/chat');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { Authorization: `Bearer ${token}` },
  (frame) => {
    console.log('Connected:', frame);
    
    // 채팅방 구독
    stompClient.subscribe('/topic/chat/{roomId}', (message) => {
      const chatMessage = JSON.parse(message.body);
      console.log('Received:', chatMessage);
    });
  }
);
```

**메시지 전송:**
```javascript
stompClient.send('/app/chat/send', {}, JSON.stringify({
  roomId: 'room123',
  senderId: 'user123',
  message: '안녕하세요!',
  type: 'CHAT'
}));
```

**메시지 타입:**
- `CHAT`: 일반 채팅 메시지
- `JOIN`: 채팅방 입장
- `LEAVE`: 채팅방 퇴장

---

## 🏥 시스템 상태

### 10. Health Check
**Endpoint:** `GET /health`

**Response:** `200 OK`
```json
{
  "service": "unimeet-backend",
  "status": "UP",
  "timestamp": "2025-10-13T17:41:43"
}
```

---

## ⚠️ 에러 처리

### 공통 에러 응답 형식
```json
{
  "timestamp": "2025-10-13T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "상세 에러 메시지"
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청 (유효성 검증 실패)
- `401 Unauthorized`: 인증 실패 (토큰 없음/만료)
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
- `500 Internal Server Error`: 서버 오류

---

## 🔧 개발 환경 설정

### CORS 설정
프론트엔드 개발 서버 주소가 다르면 백엔드에 추가 필요:

```java
// SecurityConfig.java
.allowedOrigins("http://localhost:3000", "http://localhost:19006", "YOUR_FRONTEND_URL")
```

### 환경 변수
```properties
# 백엔드 서버
SERVER_URL=http://localhost:8080

# JWT 토큰 만료 시간
TOKEN_EXPIRY=86400000  # 24시간 (밀리초)
```

---

## 📝 테스트 계정

### 개발용 테스트 계정
```
이메일: test.student@kku.ac.kr
비밀번호: password123
```

---

## 🚀 API 테스트 예시 (JavaScript/TypeScript)

### 회원가입 → 이메일 인증 → 로그인 플로우
```typescript
// 1. 회원가입
const signupResponse = await fetch('http://localhost:8080/auth/signup', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'student@kku.ac.kr',
    password: 'password123',
    name: '김건국',
    nickname: '건국이',
    studentId: '202020945',
    department: '컴퓨터공학과',
    birth: '2001-03-15',
    phone: '010-1234-5678',
    gender: '남'
  })
});

// 2. 이메일 인증 코드 발송
await fetch('http://localhost:8080/auth/send-verification', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'student@kku.ac.kr' })
});

// 3. 이메일 인증 (사용자가 입력한 코드)
await fetch('http://localhost:8080/auth/verify-email', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'student@kku.ac.kr',
    code: '123456'
  })
});

// 4. 로그인
const loginResponse = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'student@kku.ac.kr',
    password: 'password123'
  })
});

const { token } = await loginResponse.json();

// 5. 인증이 필요한 API 호출
const profileResponse = await fetch('http://localhost:8080/api/user/profile', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

const profile = await profileResponse.json();
```

---

## 📞 문의 및 지원

### 백엔드 개발자 연락처
- 이메일: [백엔드 개발자 이메일]
- GitHub: [저장소 링크]

### API 문서
- Swagger UI: `http://localhost:8080/swagger-ui.html` (개발 중)

---

## ✅ 체크리스트

프론트엔드 개발 시작 전 확인사항:

- [ ] 백엔드 서버 실행 확인 (`http://localhost:8080/health`)
- [ ] CORS 설정 확인 (프론트엔드 URL 허용)
- [ ] JWT 토큰 저장 방식 결정 (localStorage, sessionStorage, cookie)
- [ ] 에러 처리 로직 구현
- [ ] 토큰 만료 시 재로그인 플로우 구현
- [ ] WebSocket 연결 테스트

---

**🎉 백엔드 API 준비 완료!**

모든 API가 정상 동작하며 프론트엔드 연동 준비가 완료되었습니다.