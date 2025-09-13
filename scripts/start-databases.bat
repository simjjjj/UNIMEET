@echo off
echo Starting MongoDB and Redis for development...

REM Check if Docker Desktop is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker Desktop is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Start MongoDB and Redis
echo Starting databases...
docker-compose -f docker-compose.dev.yml up -d mongodb redis

REM Wait a moment for services to start
timeout /t 3 /nobreak >nul

REM Check if services are running
echo Checking service status...
docker-compose -f docker-compose.dev.yml ps

echo.
echo ✅ Databases are starting up!
echo.
echo 📊 Access points:
echo   - MongoDB: localhost:27017
echo   - Redis: localhost:6379
echo.
echo 🔧 Optional management tools:
echo   To start MongoDB Express: docker-compose -f docker-compose.dev.yml up -d mongo-express
echo   To start Redis Commander: docker-compose -f docker-compose.dev.yml up -d redis-commander
echo   Then visit:
echo   - MongoDB Express: http://localhost:8081 (admin/admin123)
echo   - Redis Commander: http://localhost:8082
echo.
echo To stop databases: docker-compose -f docker-compose.dev.yml down
echo.
pause