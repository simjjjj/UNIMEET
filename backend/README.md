# UniMeet Backend

대학생 매칭 애플리케이션의 백엔드 서버입니다.

## 🚀 기술 스택

- **Framework**: Spring Boot 3.2.3
- **Language**: Java 17
- **Database**: MongoDB (메인), Redis (캐시/세션)
- **Security**: Spring Security + JWT
- **Real-time**: WebSocket (STOMP)
- **Testing**: JUnit 5, TestContainers
- **Documentation**: Swagger/OpenAPI 3

## 📋 주요 기능

### 🔐 인증/인가
- JWT 기반 로그인/회원가입
- Spring Security 통합
- WebSocket JWT 인증

### 👥 사용자 관리
- 프로필 관리 (MBTI, 관심사, 성격 키워드)
- 이상형 설정
- 프로필 업데이트

### 💕 매칭 시스템
- 호환성 기반 매칭 알고리즘
  - MBTI 호환성 (30%)
  - 관심사 유사도 (25%)
  - 성격 키워드 (25%)
  - 이상형 매칭 (20%)
- 매칭 요청/수락/거절
- 페이지네이션 지원

### 💬 실시간 채팅
- WebSocket 기반 실시간 메시징
- 채팅방 관리
- 메시지 읽음 처리
- 채팅 히스토리 저장

### 🚀 성능 최적화
- Redis 캐싱
- 페이지네이션
- 데이터베이스 인덱싱

## 🛠️ 개발 환경 설정

### 필수 요구사항
- Java 17+
- MongoDB 6.0+
- Redis 7.0+

### 로컬 개발 환경 시작

#### 1. 저장소 클론
```bash
git clone <repository-url>
cd backend
```

#### 2. 환경변수 설정
```bash
cp .env.example .env
# .env 파일을 편집하여 필요한 설정 변경
```

#### 3. 데이터베이스 시작 (Docker 사용)
```bash
# MongoDB 시작
docker run -d -p 27017:27017 --name mongo mongo:6.0

# Redis 시작
docker run -d -p 6379:6379 --name redis redis:7-alpine
```

#### 4. 애플리케이션 시작
```bash
# 스크립트 사용 (권장)
chmod +x scripts/start-local.sh
./scripts/start-local.sh

# 또는 직접 실행
./gradlew bootRun
```

### Windows 사용자
```cmd
scripts\start-local.bat
```

## 🧪 테스트

### 테스트 실행
```bash
# 스크립트 사용
chmod +x scripts/run-tests.sh
./scripts/run-tests.sh

# 또는 직접 실행
./gradlew test
```

### 테스트 커버리지
```bash
./gradlew jacocoTestReport
# 결과: build/reports/jacoco/test/html/index.html
```

## 📖 API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔧 주요 엔드포인트

### 인증
- `POST /auth/signup` - 회원가입
- `POST /auth/login` - 로그인

### 사용자
- `GET /api/user/profile` - 내 프로필 조회
- `PUT /api/user/profile` - 프로필 업데이트
- `GET /api/user/{userId}` - 사용자 프로필 조회

### 매칭
- `GET /api/matches/candidates` - 매칭 후보 조회
- `POST /api/matches/request` - 매칭 요청
- `POST /api/matches/{matchId}/accept` - 매칭 수락
- `POST /api/matches/{matchId}/reject` - 매칭 거절
- `GET /api/matches/my-matches` - 내 매칭 목록

### 채팅
- `POST /api/chat/rooms` - 채팅방 생성/조회
- `GET /api/chat/rooms/{roomId}/messages` - 메시지 목록
- `GET /api/chat/rooms/user/{userId}` - 사용자 채팅방 목록

### WebSocket
- **연결**: `/ws`
- **메시지 전송**: `/app/chat/{roomId}`
- **구독**: `/topic/chat/{roomId}`

## 🏗️ 프로젝트 구조

```
src/main/java/com/unimeet/backend/
├── config/          # 설정 클래스
├── controller/      # REST 컨트롤러
├── domain/          # 도메인 모델
├── dto/             # 데이터 전송 객체
├── exception/       # 커스텀 예외
├── repository/      # 데이터 접근 계층
├── security/        # 보안 관련
└── service/         # 비즈니스 로직
```

## 🐳 Docker 배포

### Docker Compose 사용
```bash
# 환경변수 설정
cp .env.docker .env

# 컨테이너 시작
docker-compose up -d
```

### 개별 Docker 실행
```bash
# 이미지 빌드
docker build -t unimeet-backend .

# 컨테이너 실행
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET=your-secret-key \
  unimeet-backend
```

## 🔒 보안 고려사항

- JWT 시크릿은 반드시 환경변수로 설정
- 프로덕션에서는 HTTPS 사용 필수
- CORS 설정을 프로덕션 도메인으로 제한
- 입력값 검증 및 SQL 인젝션 방지
- 비밀번호 BCrypt 해시화

## 📊 모니터링

### 헬스체크
- `GET /health` - 애플리케이션 상태 확인

### 로깅
- 애플리케이션 로그: `logs/application.log`
- 에러 로그: `logs/error.log`

## 🤝 기여하기

1. 이슈 생성 또는 기존 이슈 확인
2. 브랜치 생성: `git checkout -b feature/새기능`
3. 변경사항 커밋: `git commit -m '새 기능 추가'`
4. 브랜치 푸시: `git push origin feature/새기능`
5. Pull Request 생성

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다.

## 🆘 문제 해결

### 자주 발생하는 문제

1. **MongoDB 연결 실패**
   ```bash
   # MongoDB가 실행 중인지 확인
   docker ps | grep mongo
   ```

2. **Redis 연결 실패**
   ```bash
   # Redis가 실행 중인지 확인
   docker ps | grep redis
   ```

3. **JWT 토큰 오류**
   - `.env` 파일의 `JWT_SECRET` 설정 확인
   - 토큰 만료 시간 확인

4. **테스트 실패**
   ```bash
   # TestContainers용 Docker 확인
   docker --version
   ```

### 로그 확인
```bash
# 애플리케이션 로그
tail -f logs/application.log

# Docker 로그
docker logs unimeet-backend
```