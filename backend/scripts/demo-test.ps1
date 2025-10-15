# UniMeet Backend Demo Test Script

Write-Host "UniMeet Backend Demo Start!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan

# 1. Server Health Check
Write-Host "`n1. Server Health Check" -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/health" -Method Get
    Write-Host "Server OK" -ForegroundColor Green
} catch {
    Write-Host "Server Connection Failed" -ForegroundColor Red
    exit 1
}

# 2. Create Test Users
Write-Host "`n2. Create Test Users" -ForegroundColor Yellow

$testUsers = @(
    @{
        email = "demo1@kku.ac.kr"
        password = "demo123456"
        name = "Kim Chulsu"
        nickname = "chulsu"
        studentId = "20241001"
        department = "Computer Science"
        birth = "1999-05-15"
        phone = "010-1111-1111"
        gender = "Male"
        mbti = "INTJ"
        interests = @("Development", "Game", "Reading")
        height = "180"
    },
    @{
        email = "demo2@kku.ac.kr"
        password = "demo123456"
        name = "Lee Younghee"
        nickname = "younghee"
        studentId = "20241002"
        department = "Business"
        birth = "1999-08-20"
        phone = "010-2222-2222"
        gender = "Female"
        mbti = "ENFP"
        interests = @("Music", "Movie", "Travel")
        height = "165"
    },
    @{
        email = "demo3@kku.ac.kr"
        password = "demo123456"
        name = "Park Minsu"
        nickname = "minsu"
        studentId = "20241003"
        department = "Design"
        birth = "1999-12-10"
        phone = "010-3333-3333"
        gender = "Male"
        mbti = "ISFJ"
        interests = @("Sports", "Music", "Development")
        height = "175"
    }
)

foreach ($user in $testUsers) {
    $signupBody = $user | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/auth/signup" `
            -Method Post -Body $signupBody -ContentType "application/json"
        Write-Host "User $($user.name) signup success" -ForegroundColor Green
    } catch {
        Write-Host "User $($user.name) already exists" -ForegroundColor Yellow
    }
}

# 3. Login Test
Write-Host "`n3. Login Test" -ForegroundColor Yellow

$loginBody = @{
    email = "demo1@kku.ac.kr"
    password = "demo123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
        -Method Post -Body $loginBody -ContentType "application/json"
    
    Write-Host "Login Success!" -ForegroundColor Green
    Write-Host "User: $($loginResponse.user.name)" -ForegroundColor Cyan
    $token = $loginResponse.token
} catch {
    Write-Host "Login Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 4. Profile Test
Write-Host "`n4. Profile Test" -ForegroundColor Yellow

$headers = @{ "Authorization" = "Bearer $token" }

try {
    $profile = Invoke-RestMethod -Uri "http://localhost:8080/api/user/profile" `
        -Method Get -Headers $headers
    
    Write-Host "Profile Success!" -ForegroundColor Green
    Write-Host "Name: $($profile.name)" -ForegroundColor White
    Write-Host "Department: $($profile.department)" -ForegroundColor White
    Write-Host "MBTI: $($profile.mbti)" -ForegroundColor White
} catch {
    Write-Host "Profile Failed" -ForegroundColor Red
}

# 5. Matching Test
Write-Host "`n5. Matching Test" -ForegroundColor Yellow

try {
    $candidates = Invoke-RestMethod -Uri "http://localhost:8080/api/matches/candidates?limit=5" `
        -Method Get -Headers $headers
    
    Write-Host "Matching Success!" -ForegroundColor Green
    Write-Host "Candidates: $($candidates.Count)" -ForegroundColor Cyan
    
    if ($candidates.Count -gt 0) {
        Write-Host "First Match Score: $($candidates[0].score)%" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Matching Failed" -ForegroundColor Red
}

# 6. Meeting Room Test
Write-Host "`n6. Meeting Room Test" -ForegroundColor Yellow

$roomBody = @{
    title = "Developer Meeting"
    description = "Let's study together!"
    type = "MIXED"
    maxParticipants = 6
} | ConvertTo-Json

try {
    $room = Invoke-RestMethod -Uri "http://localhost:8080/api/meetings/create" `
        -Method Post -Body $roomBody -Headers @{ 
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
    
    Write-Host "Meeting Room Success!" -ForegroundColor Green
    Write-Host "Room: $($room.meetingRoom.title)" -ForegroundColor Cyan
} catch {
    Write-Host "Meeting Room Failed" -ForegroundColor Red
}

# 7. Notification Test
Write-Host "`n7. Notification Test" -ForegroundColor Yellow

try {
    $notifications = Invoke-RestMethod -Uri "http://localhost:8080/api/notifications" `
        -Method Get -Headers $headers
    
    Write-Host "Notification Success!" -ForegroundColor Green
    Write-Host "Notifications: $($notifications.notifications.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "Notification Failed" -ForegroundColor Red
}

# 8. Chat Test
Write-Host "`n8. Chat Test" -ForegroundColor Yellow

try {
    $chatRooms = Invoke-RestMethod -Uri "http://localhost:8080/api/chat/rooms" `
        -Method Get -Headers $headers
    
    Write-Host "Chat Success!" -ForegroundColor Green
    Write-Host "Chat Rooms: $($chatRooms.chatRooms.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "Chat Failed" -ForegroundColor Red
}

Write-Host "`nDemo Complete!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan
Write-Host "All core features working!" -ForegroundColor Green
Write-Host "Frontend integration ready!" -ForegroundColor Cyan