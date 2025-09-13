#!/usr/bin/env python3
"""
UniMeet AI 매칭 서비스 시작 스크립트
"""

import subprocess
import sys
import os

def install_requirements():
    """필요한 패키지 설치"""
    print("📦 Python 패키지 설치 중...")
    try:
        subprocess.check_call([sys.executable, "-m", "pip", "install", "-r", "requirements.txt"])
        print("✅ 패키지 설치 완료")
    except subprocess.CalledProcessError as e:
        print(f"❌ 패키지 설치 실패: {e}")
        sys.exit(1)

def start_service():
    """AI 서비스 시작"""
    print("🤖 UniMeet AI 매칭 서비스 시작 중...")
    print("📍 서비스 주소: http://localhost:8001")
    print("📖 API 문서: http://localhost:8001/docs")
    print("🛑 종료하려면 Ctrl+C를 누르세요")
    print("-" * 50)
    
    try:
        subprocess.run([
            sys.executable, "-m", "uvicorn", 
            "ai_matching_service:app", 
            "--host", "0.0.0.0", 
            "--port", "8001",
            "--reload"
        ])
    except KeyboardInterrupt:
        print("\n🛑 AI 서비스가 종료되었습니다.")
    except Exception as e:
        print(f"❌ 서비스 시작 실패: {e}")

if __name__ == "__main__":
    print("🎯 UniMeet AI 매칭 서비스")
    print("=" * 50)
    
    # 현재 디렉토리 확인
    if not os.path.exists("ai_matching_service.py"):
        print("❌ ai_matching_service.py 파일을 찾을 수 없습니다.")
        print("💡 ai 폴더에서 실행해주세요.")
        sys.exit(1)
    
    # 패키지 설치
    install_requirements()
    
    # 서비스 시작
    start_service()