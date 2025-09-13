#!/bin/bash

# 로컬 개발 환경 시작 스크립트

echo "🚀 UniMeet 백엔드 로컬 개발 환경을 시작합니다..."

# 환경변수 파일 확인
if [ ! -f .env ]; then
    echo "📋 .env 파일이 없습니다. .env.example을 복사하여 .env 파일을 생성합니다."
    cp .env.example .env
    echo "⚠️  .env 파일을 확인하고 필요한 설정을 수정해주세요."
fi

# MongoDB와 Redis가 실행 중인지 확인
echo "🔍 MongoDB와 Redis 연결을 확인합니다..."

# MongoDB 연결 확인
if ! nc -z localhost 27017; then
    echo "❌ MongoDB가 실행되지 않았습니다. MongoDB를 시작해주세요."
    echo "   Docker로 시작: docker run -d -p 27017:27017 --name mongo mongo:6.0"
    exit 1
fi

# Redis 연결 확인
if ! nc -z localhost 6379; then
    echo "❌ Redis가 실행되지 않았습니다. Redis를 시작해주세요."
    echo "   Docker로 시작: docker run -d -p 6379:6379 --name redis redis:7-alpine"
    exit 1
fi

echo "✅ 데이터베이스 연결이 확인되었습니다."

# 환경변수 로드
export $(cat .env | xargs)

# 애플리케이션 시작
echo "🏃 Spring Boot 애플리케이션을 시작합니다..."
./gradlew bootRun

echo "🎉 애플리케이션이 시작되었습니다!"
echo "📖 API 문서: http://localhost:8080/swagger-ui/index.html"
echo "🏥 헬스체크: http://localhost:8080/health"