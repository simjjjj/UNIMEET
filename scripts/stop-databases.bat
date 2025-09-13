@echo off
echo Stopping MongoDB and Redis...

docker-compose -f docker-compose.dev.yml down

echo.
echo ✅ Databases stopped!
echo.
pause