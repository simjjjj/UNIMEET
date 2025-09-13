#!/bin/bash

echo "UniMeet 개발 환경 시작..."

echo ""
echo "1. Docker 컨테이너 시작 중..."
docker-compose up -d mongodb redis

echo ""
echo "2. 백엔드 빌드 중..."
cd backend
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo "백엔드 빌드 실패!"
    exit 1
fi

echo ""
echo "3. 프론트엔드 의존성 설치 중..."
cd ../frontend
npm install
if [ $? -ne 0 ]; then
    echo "프론트엔드 의존성 설치 실패!"
    exit 1
fi

echo ""
echo "4. 모든 서비스 시작..."
cd ..
docker-compose up --build

