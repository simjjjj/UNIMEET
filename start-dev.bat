@echo off
echo UniMeet 개발 환경 시작...

echo.
echo 1. Docker 컨테이너 시작 중...
docker-compose up -d mongodb redis

echo.
echo 2. 백엔드 빌드 중...
cd backend
call gradlew clean build -x test
if %errorlevel% neq 0 (
    echo 백엔드 빌드 실패!
    pause
    exit /b 1
)

echo.
echo 3. 프론트엔드 의존성 설치 중...
cd ..\frontend
call npm install
if %errorlevel% neq 0 (
    echo 프론트엔드 의존성 설치 실패!
    pause
    exit /b 1
)

echo.
echo 4. 모든 서비스 시작...
cd ..
docker-compose up --build

pause

