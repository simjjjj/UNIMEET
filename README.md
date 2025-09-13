# UniMeet 🎓💕

대학생 소개팅 매칭 플랫폼 - Spring Boot + React Native + MongoDB + Redis + WebSocket

## 🚀 기술 스택

### Backend
- **Spring Boot 3.2.3** - 메인 백엔드 프레임워크
- **MongoDB** - NoSQL 데이터베이스
- **Redis** - 캐싱 및 세션 관리
- **WebSocket** - 실시간 채팅
- **JWT** - 사용자 인증
- **Spring Security** - 보안
- **Docker** - 컨테이너화

### Frontend  
- **React Native** - 모바일 앱 개발
- **TypeScript** - 타입 안정성
- **Redux Toolkit** - 상태 관리
- **Expo** - 개발 및 빌드 도구
- **AsyncStorage** - 로컬 저장소
- **STOMP** - WebSocket 통신

### Infrastructure
- **Docker Compose** - 서비스 오케스트레이션
- **MongoDB Compass** - 데이터베이스 GUI
- **Postman** - API 테스트

## 📁 프로젝트 구조

```
unimeet/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/unimeet/backend/
│   │       ├── config/      # 설정 클래스들
│   │       ├── controller/  # REST API 컨트롤러
│   │       ├── domain/      # 엔티티 클래스들
│   │       ├── dto/         # 데이터 전송 객체
│   │       ├── repository/  # 데이터 접근 계층
│   │       ├── security/    # JWT 및 보안 설정
│   │       └── service/     # 비즈니스 로직
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-docker.properties
│   ├── build.gradle
│   └── Dockerfile
├── frontend/                # React Native 프론트엔드
│   ├── Screens/            # 화면 컴포넌트들
│   │   ├── Auth/           # 로그인/회원가입
│   │   ├── Chat/           # 채팅 관련
│   │   ├── Home/           # 홈 화면
│   │   ├── Lounge/         # 라운지
│   │   ├── Meeting/        # 미팅
│   │   └── Profile/        # 프로필
│   ├── services/           # API 및 WebSocket 서비스
│   ├── store/              # Redux 상태 관리
│   ├── navigation/         # 내비게이션 설정
│   ├── component/          # 공통 컴포넌트
│   ├── package.json
│   └── Dockerfile
├── ai/                     # AI 서비스 (Python)
├── docker-compose.yml      # 전체 서비스 오케스트레이션
├── start-dev.bat          # Windows 개발 환경 시작 스크립트
├── start-dev.sh           # Linux/Mac 개발 환경 시작 스크립트
└── README.md
```

## 🔧 설치 및 실행

### 사전 요구사항
- **Docker Desktop** 설치
- **Node.js** (16 이상)
- **Java 17** 이상
- **MongoDB Compass** (선택사항)

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd unimeet
```

### 2. 자동 실행 (권장)

**Windows:**
```cmd
start-dev.bat
```

**Linux/Mac:**
```bash
./start-dev.sh
```

### 3. 수동 실행

**1) MongoDB & Redis 시작:**
```bash
docker-compose up -d mongodb redis
```

**2) 백엔드 빌드 및 실행:**
```bash
cd backend
./gradlew clean build
cd ..
docker-compose up backend
```

**3) 프론트엔드 실행:**
```bash
cd frontend
npm install
npm start
```

## 📱 접속 방법

### 백엔드 API
- **로컬:** http://localhost:8080
- **헬스체크:** http://localhost:8080/health

### 프론트엔드 앱
- **Expo 개발 서버:** http://localhost:19006
- **모바일 앱:** Expo Go 앱에서 QR 코드 스캔

### 데이터베이스
- **MongoDB:** localhost:27017
- **MongoDB Compass 연결:** `mongodb://localhost:27017`
- **Redis:** localhost:6379

## 🔌 주요 API 엔드포인트

### 인증
- `POST /auth/signup` - 회원가입
- `POST /auth/login` - 로그인

### 사용자
- `GET /api/user/profile` - 프로필 조회
- `PUT /api/user/profile` - 프로필 수정

### 채팅
- `GET /api/chat/rooms` - 채팅방 목록
- `POST /api/chat/rooms/{roomId}/messages` - 메시지 전송

### WebSocket
- **연결:** `/ws`
- **채팅:** `/app/chat/{roomId}`
- **구독:** `/topic/chat/{roomId}`

## 🛠 개발 도구

### MongoDB Compass 사용법
1. MongoDB Compass 실행
2. 연결 문자열: `mongodb://localhost:27017` 입력
3. 연결 후 `unimeet` 데이터베이스 확인

### API 테스트
1. Postman 또는 Insomnia 사용
2. 로그인 후 JWT 토큰을 Authorization 헤더에 추가:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

### 실시간 채팅 테스트
1. WebSocket 클라이언트 도구 사용
2. `/ws` 엔드포인트로 연결
3. STOMP 프로토콜로 메시지 전송

## 🐳 Docker 구성

### 서비스들
- **backend:** Spring Boot 애플리케이션 (포트 8080)
- **frontend:** React Native/Expo 개발 서버 (포트 3000)
- **mongodb:** MongoDB 데이터베이스 (포트 27017)
- **redis:** Redis 캐시 서버 (포트 6379)
- **ai:** Python AI 서비스 (포트 5000)

### 네트워크
모든 서비스는 동일한 Docker 네트워크에서 통신합니다.

## 🔐 환경 설정

### 백엔드 환경변수
- `SPRING_PROFILES_ACTIVE=docker`
- `JWT_SECRET`: JWT 토큰 시크릿 키
- `MONGODB_URI`: MongoDB 연결 문자열
- `REDIS_HOST`: Redis 호스트

### 프론트엔드 환경변수
- `API_BASE_URL`: 백엔드 API 기본 URL
- `WEBSOCKET_URL`: WebSocket 서버 URL

## 🚀 배포

### 프로덕션 빌드
```bash
# 백엔드
cd backend
./gradlew build
docker build -t unimeet-backend .

# 프론트엔드  
cd frontend
npm run build
docker build -t unimeet-frontend .
```

### Docker Compose 프로덕션
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

## 📞 문의

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 생성해주세요.

---

## 🔍 트러블슈팅

### 일반적인 문제들

**1. 포트 충돌**
```bash
# 사용중인 포트 확인
netstat -ano | findstr :8080
# 프로세스 종료
taskkill /PID <PID> /F
```

**2. Docker 컨테이너 재시작**
```bash
docker-compose down
docker-compose up --build
```

**3. 캐시 정리**
```bash
# Docker 캐시 정리
docker system prune -a

# npm 캐시 정리
cd frontend
npm cache clean --force
```

**4. 데이터베이스 초기화**
```bash
docker-compose down -v
docker-compose up -d mongodb
```