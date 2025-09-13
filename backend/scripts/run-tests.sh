#!/bin/bash

# 테스트 실행 스크립트

echo "🧪 UniMeet 백엔드 테스트를 실행합니다..."

# 테스트 환경 설정
export SPRING_PROFILES_ACTIVE=test

# 단위 테스트 실행
echo "📋 단위 테스트를 실행합니다..."
./gradlew test --info

# 테스트 커버리지 확인
echo "📊 테스트 커버리지를 확인합니다..."
./gradlew jacocoTestReport

# 결과 출력
if [ $? -eq 0 ]; then
    echo "✅ 모든 테스트가 성공했습니다!"
    echo "📊 커버리지 리포트: build/reports/jacoco/test/html/index.html"
    echo "📋 테스트 리포트: build/reports/tests/test/index.html"
else
    echo "❌ 테스트가 실패했습니다."
    exit 1
fi