@echo off
echo 🚀 UniMeet 백엔드 로컬 개발 환경을 시작합니다...

REM 환경변수 파일 확인
if not exist .env (
    echo 📋 .env 파일이 없습니다. .env.example을 복사하여 .env 파일을 생성합니다.
    copy .env.example .env
    echo ⚠️  .env 파일을 확인하고 필요한 설정을 수정해주세요.
)

echo 🔍 MongoDB와 Redis 연결을 확인합니다...

REM MongoDB 연결 확인 (간단한 방법)
netstat -an | find "27017" >nul
if errorlevel 1 (
    echo ❌ MongoDB가 실행되지 않았습니다. MongoDB를 시작해주세요.
    echo    Docker로 시작: docker run -d -p 27017:27017 --name mongo mongo:6.0
    pause
    exit /b 1
)

REM Redis 연결 확인
netstat -an | find "6379" >nul
if errorlevel 1 (
    echo ❌ Redis가 실행되지 않았습니다. Redis를 시작해주세요.
    echo    Docker로 시작: docker run -d -p 6379:6379 --name redis redis:7-alpine
    pause
    exit /b 1
)

echo ✅ 데이터베이스 연결이 확인되었습니다.

REM 환경변수 설정
set SPRING_PROFILES_ACTIVE=local

REM 애플리케이션 시작
echo 🏃 Spring Boot 애플리케이션을 시작합니다...
gradlew.bat bootRun

echo 🎉 애플리케이션이 시작되었습니다!
echo 📖 API 문서: http://localhost:8080/swagger-ui/index.html
echo 🏥 헬스체크: http://localhost:8080/health
pause