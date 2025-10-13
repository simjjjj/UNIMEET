@echo off
echo 🎯 UniMeet AI 매칭 서비스
echo ==================================================

cd /d "%~dp0"

echo 📦 Python 패키지 설치 중...
pip install -r requirements.txt

if %errorlevel% neq 0 (
    echo ❌ 패키지 설치 실패
    pause
    exit /b 1
)

echo ✅ 패키지 설치 완료
echo.
echo 🤖 UniMeet AI 매칭 서비스 시작 중...
echo 📍 서비스 주소: http://localhost:8001
echo 📖 API 문서: http://localhost:8001/docs
echo 🛑 종료하려면 Ctrl+C를 누르세요
echo --------------------------------------------------

python -m uvicorn ai_matching_service:app --host 0.0.0.0 --port 8001 --reload

pause