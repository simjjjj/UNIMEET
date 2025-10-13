# UniMeet API 테스트 명령어

## 🚀 빠른 시작

### 1. 서버 시작 확인
```powershell
# Health Check
Invoke-RestMethod -Uri "http://localhost:8080/health" -Method Get
```

---

## 👤 사용자 관리

### 회원가입
```powershell
$body = @{
    email = "test@kku.ac.kr"
    password = "password123"
    name = "Test User"
    nickname = "TestNick"
    studentId = "20240001"
    department = "Computer Science"
    birth = "1999-01-01"
    phone = "010-1234-5678"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/auth/signup" `
    -Method Post -Body $body -ContentType "application/json"
```

### 로그인
```powershell
$loginBody = @{
    email = "test@kku.ac.kr"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
    -Method Post -Body $loginBody -ContentType "application/json"

$token = $response.token
Write-Host "Token: $token"
```

### 내 프로필 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/user/profile" `
    -Method Get -Headers $headers
```

---

## 💑 매칭 시스템

### 매칭 후보 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/candidates?limit=10" `
    -Method Get -Headers $headers
```

### 매칭 요청 보내기
```powershell
$headers = @{ 
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$matchBody = @{
    targetId = "USER_ID_HERE"
    message = "Hello! Let's connect!"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/request" `
    -Method Post -Body $matchBody -Headers $headers
```

### 받은 매칭 요청 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/received" `
    -Method Get -Headers $headers
```

### 보낸 매칭 요청 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/sent" `
    -Method Get -Headers $headers
```

### 매칭 수락
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$matchId = "MATCH_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/$matchId/accept" `
    -Method Post -Headers $headers
```

### 매칭 거절
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$matchId = "MATCH_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/$matchId/reject" `
    -Method Post -Headers $headers
```

### 수락된 매칭 목록
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/accepted" `
    -Method Get -Headers $headers
```

---

## 🏠 미팅방 시스템

### 미팅방 생성
```powershell
$headers = @{ 
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$roomBody = @{
    title = "Study Group"
    description = "Algorithm study group"
    type = "MIXED"
    maxParticipants = 6
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/create" `
    -Method Post -Body $roomBody -Headers $headers
```

### 참가 가능한 미팅방 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/available" `
    -Method Get -Headers $headers
```

### 내 미팅방 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/my" `
    -Method Get -Headers $headers
```

### 미팅방 참가
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$roomId = "ROOM_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/$roomId/join" `
    -Method Post -Headers $headers
```

### 미팅방 나가기
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$roomId = "ROOM_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/$roomId/leave" `
    -Method Post -Headers $headers
```

### 타입별 미팅방 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

# PAIR 타입
Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/type/pair" `
    -Method Get -Headers $headers

# MIXED 타입
Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/type/mixed" `
    -Method Get -Headers $headers
```

---

## 🔔 알림 시스템

### 알림 목록 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/notifications" `
    -Method Get -Headers $headers
```

### 읽지 않은 알림 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/unread" `
    -Method Get -Headers $headers
```

### 읽지 않은 알림 개수
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/unread/count" `
    -Method Get -Headers $headers
```

### 알림 읽음 처리
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$notificationId = "NOTIFICATION_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/$notificationId/read" `
    -Method Post -Headers $headers
```

### 모든 알림 읽음 처리
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/read-all" `
    -Method Post -Headers $headers
```

### 알림 삭제
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$notificationId = "NOTIFICATION_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/$notificationId" `
    -Method Delete -Headers $headers
```

---

## 💬 채팅 시스템

### 채팅방 목록 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/chat/rooms" `
    -Method Get -Headers $headers
```

### 채팅 메시지 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$roomId = "ROOM_ID_HERE"

Invoke-RestMethod -Uri "http://localhost:8080/api/chat/rooms/$roomId/messages" `
    -Method Get -Headers $headers
```

---

## 🔄 전체 플로우 테스트

### 1단계: 회원가입 및 로그인
```powershell
# 회원가입
$signupBody = @{
    email = "user1@kku.ac.kr"
    password = "pass123"
    name = "User One"
    nickname = "User1"
    studentId = "20240001"
    department = "CS"
    birth = "1999-01-01"
    phone = "010-1111-1111"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/auth/signup" `
    -Method Post -Body $signupBody -ContentType "application/json"

# 로그인
$loginBody = @{
    email = "user1@kku.ac.kr"
    password = "pass123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
    -Method Post -Body $loginBody -ContentType "application/json"

$token = $response.token
```

### 2단계: 매칭 후보 조회
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

$candidates = Invoke-RestMethod -Uri "http://localhost:8080/api/matches/candidates?limit=5" `
    -Method Get -Headers $headers

Write-Host "Found $($candidates.matches.Count) candidates"
```

### 3단계: 매칭 요청
```powershell
$headers = @{ 
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$targetId = $candidates.matches[0].targetId

$matchBody = @{
    targetId = $targetId
    message = "Hi! Want to connect?"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/matches/request" `
    -Method Post -Body $matchBody -Headers $headers
```

### 4단계: 알림 확인
```powershell
$headers = @{ "Authorization" = "Bearer $token" }

$notifications = Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/unread" `
    -Method Get -Headers $headers

Write-Host "Unread notifications: $($notifications.unreadCount)"
```

### 5단계: 미팅방 생성
```powershell
$headers = @{ 
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$roomBody = @{
    title = "Study Group"
    description = "Let's study together"
    type = "MIXED"
    maxParticipants = 4
} | ConvertTo-Json

$room = Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/create" `
    -Method Post -Body $roomBody -Headers $headers

Write-Host "Created room: $($room.meetingRoom.id)"
```

---

## 🎯 자동 테스트 스크립트

간단한 테스트를 위해 다음 스크립트를 실행하세요:

```powershell
cd backend/scripts
./api-test.ps1
```

---

## 📝 참고사항

### 토큰 저장
로그인 후 받은 토큰을 변수에 저장해두면 편리합니다:
```powershell
$token = "YOUR_JWT_TOKEN_HERE"
$headers = @{ "Authorization" = "Bearer $token" }
```

### 에러 처리
API 호출 시 에러가 발생하면 다음과 같이 확인할 수 있습니다:
```powershell
try {
    $response = Invoke-RestMethod -Uri "..." -Method Get -Headers $headers
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Write-Host "Response: $($_.ErrorDetails.Message)"
}
```

### JSON 포맷팅
응답을 보기 좋게 출력하려면:
```powershell
$response | ConvertTo-Json -Depth 10
```

---

## 🎉 완료!

모든 API가 정상 작동합니다. 프론트엔드 개발을 시작하세요!
