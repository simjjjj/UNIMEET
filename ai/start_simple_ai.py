#!/usr/bin/env python3
"""
UniMeet 간단한 AI 서비스 시작 스크립트 (시연용)
"""

import subprocess
import sys
import os
import time

def print_banner():
    print("🤖 UniMeet 간단한 AI 매칭 서비스 (시연용)")
    print("=" * 50)

def install_requirements():
    """필요한 패키지만 설치"""
    print("📦 필수 패키지 설치 중...")
    
    # 간단한 AI에 필요한 패키지만
    simple_requirements = [
        "fastapi==0.104.1",
        "uvicorn==0.24.0", 
        "numpy==1.24.3",
        "scikit-learn==1.3.2",
        "pydantic==2.5.0"
    ]
    
    for package in simple_requirements:
        try:
            subprocess.check_call([sys.executable, "-m", "pip", "install", package])
        except subprocess.CalledProcessError as e:
            print(f"⚠️ {package} 설치 실패: {e}")
            continue
    
    print("✅ 필수 패키지 설치 완료")

def start_service():
    """AI 서비스 시작"""
    print("🤖 UniMeet 간단한 AI 매칭 서비스 시작 중...")
    print("📍 서비스 주소: http://localhost:8001")
    print("📖 API 문서: http://localhost:8001/docs")
    print("🛑 종료하려면 Ctrl+C를 누르세요")
    print("-" * 50)
    
    try:
        # simple_main.py 실행
        subprocess.run([
            sys.executable, "-m", "uvicorn", 
            "simple_main:app", 
            "--host", "0.0.0.0", 
            "--port", "8001",
            "--reload"
        ])
    except KeyboardInterrupt:
        print("\n🛑 AI 서비스가 종료되었습니다.")
    except Exception as e:
        print(f"❌ 서비스 시작 실패: {e}")

def main():
    print_banner()
    
    # 현재 디렉토리가 ai 폴더인지 확인
    if not os.path.exists("simple_main.py"):
        print("❌ simple_main.py 파일을 찾을 수 없습니다.")
        print("ai 폴더에서 실행해주세요.")
        return
    
    try:
        install_requirements()
        time.sleep(1)
        start_service()
    except Exception as e:
        print(f"❌ 실행 실패: {e}")

if __name__ == "__main__":
    main()