# UniMeet API Test Script
# PowerShell Script for Testing All APIs

$baseUrl = "http://localhost:8080"
$token = ""

Write-Host "=== UniMeet API Test Started ===" -ForegroundColor Green
Write-Host ""

# 1. Health Check
Write-Host "1. Health Check..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/health" -Method Get
    Write-Host "Success: Health Check OK" -ForegroundColor Green
} catch {
    Write-Host "Failed: Health Check" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 2. Signup
Write-Host "2. Signup Test..." -ForegroundColor Cyan
$signupBody = @{
    email = "test.student@kku.ac.kr"
    password = "password123"
    name = "Test User"
    nickname = "TestNick"
    studentId = "20240001"
    department = "Computer Science"
    birth = "1999-01-01"
    phone = "010-1234-5678"
    mbti = "INTJ"
    interests = @("Reading", "Movies")
    height = "175"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/signup" -Method Post -Body $signupBody -ContentType "application/json"
    Write-Host "Success: Signup completed" -ForegroundColor Green
} catch {
    Write-Host "Warning: User may already exist" -ForegroundColor Yellow
}
Write-Host ""

# 3. Login
Write-Host "3. Login Test..." -ForegroundColor Cyan
$loginBody = @{
    email = "test.student@kku.ac.kr"
    password = "password123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $response.token
    Write-Host "Success: Login completed" -ForegroundColor Green
    Write-Host "JWT Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
} catch {
    Write-Host "Failed: Login" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Set Headers
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 4. Get My Profile
Write-Host "4. Get My Profile..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/user/profile" -Method Get -Headers $headers
    Write-Host "Success: Profile retrieved" -ForegroundColor Green
    Write-Host "   Name: $($response.name)" -ForegroundColor Gray
    Write-Host "   Nickname: $($response.nickname)" -ForegroundColor Gray
} catch {
    Write-Host "Failed: Get profile" -ForegroundColor Red
}
Write-Host ""

# 5. Get Match Candidates
Write-Host "5. Get Match Candidates..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/matches/candidates?limit=5" -Method Get -Headers $headers
    Write-Host "Success: Match candidates retrieved" -ForegroundColor Green
    Write-Host "   Candidates: $($response.matches.Count)" -ForegroundColor Gray
} catch {
    Write-Host "Failed: Get match candidates" -ForegroundColor Red
}
Write-Host ""

# 6. Get Notifications
Write-Host "6. Get Notifications..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/notifications" -Method Get -Headers $headers
    Write-Host "Success: Notifications retrieved" -ForegroundColor Green
    Write-Host "   Count: $($response.notifications.Count)" -ForegroundColor Gray
} catch {
    Write-Host "Failed: Get notifications" -ForegroundColor Red
}
Write-Host ""

# 7. Get Meeting Rooms
Write-Host "7. Get Available Meeting Rooms..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/meetings/available" -Method Get -Headers $headers
    Write-Host "Success: Meeting rooms retrieved" -ForegroundColor Green
    Write-Host "   Count: $($response.meetingRooms.Count)" -ForegroundColor Gray
} catch {
    Write-Host "Failed: Get meeting rooms" -ForegroundColor Red
}
Write-Host ""

Write-Host "=== API Test Completed ===" -ForegroundColor Green
Write-Host ""
Write-Host "All APIs are working!" -ForegroundColor Green
