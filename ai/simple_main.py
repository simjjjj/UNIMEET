"""
UniMeet 간단한 AI 매칭 서비스 (시연용)
scikit-learn 기반 안정적인 매칭 시스템
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from typing import List, Dict, Optional
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="UniMeet Simple AI Matching Service", version="1.0.0")

class UserProfile(BaseModel):
    """사용자 프로필 데이터 모델"""
    user_id: str
    mbti: Optional[str] = None
    interests: List[str] = []
    personality_keywords: List[str] = []
    department: Optional[str] = None
    birth_year: Optional[int] = None
    height: Optional[int] = None

class MatchRequest(BaseModel):
    """매칭 요청 데이터 모델"""
    target_user: UserProfile
    candidate_users: List[UserProfile]
    top_k: int = 10

class MatchResult(BaseModel):
    """매칭 결과 데이터 모델"""
    user_id: str
    compatibility_score: float
    detailed_scores: Dict[str, float]

class SimpleAIMatchingService:
    """간단한 AI 매칭 서비스 클래스"""
    
    def __init__(self):
        # MBTI 호환성 매트릭스
        self.mbti_compatibility = {
            'INTJ': ['ENFP', 'ENTP', 'INFJ', 'INFP'],
            'INTP': ['ENFJ', 'ENTJ', 'INFJ', 'INFP'],
            'ENTJ': ['INFP', 'INTP', 'ENFP', 'ENTP'],
            'ENTP': ['INFJ', 'INTJ', 'ENFJ', 'ENTJ'],
            'INFJ': ['ENFP', 'ENTP', 'INFP', 'ENFJ'],
            'INFP': ['ENFJ', 'ENTJ', 'INFJ', 'ENFP'],
            'ENFJ': ['INFP', 'INTP', 'INFJ', 'ENFP'],
            'ENFP': ['INTJ', 'INFJ', 'ENFJ', 'ENTP'],
            'ISTJ': ['ESFP', 'ESTP', 'ISFJ', 'ISFP'],
            'ISFJ': ['ESFP', 'ESTP', 'ISTJ', 'ISFP'],
            'ESTJ': ['ISFP', 'ISTP', 'ESFP', 'ESTP'],
            'ESFJ': ['ISFP', 'ISTP', 'ISFJ', 'ESFP'],
            'ISTP': ['ESFJ', 'ESTJ', 'ISFJ', 'ESFP'],
            'ISFP': ['ESFJ', 'ESTJ', 'ISFJ', 'ESFP'],
            'ESTP': ['ISFJ', 'ISTJ', 'ESFJ', 'ESFP'],
            'ESFP': ['ISFJ', 'ISTJ', 'ESFJ', 'ESTP']
        }
        
        # 공통 관심사 및 성격 키워드
        self.common_interests = ['독서', '영화감상', '운동', '음악', '여행', '게임', '요리', '사진', '춤', '그림']
        self.personality_traits = ['외향적', '내향적', '논리적', '감정적', '계획적', '즉흥적', '활발한', '차분한', '창의적', '현실적']
        
        logger.info("간단한 AI 매칭 서비스 초기화 완료")

    def encode_user_features(self, user: UserProfile) -> np.ndarray:
        """사용자 특성을 벡터로 인코딩"""
        features = []
        
        # MBTI 인코딩 (16차원 원-핫)
        mbti_types = ['INTJ', 'INTP', 'ENTJ', 'ENTP', 'INFJ', 'INFP', 'ENFJ', 'ENFP',
                     'ISTJ', 'ISFJ', 'ESTJ', 'ESFJ', 'ISTP', 'ISFP', 'ESTP', 'ESFP']
        mbti_vector = [1.0 if user.mbti == mbti else 0.0 for mbti in mbti_types]
        features.extend(mbti_vector)
        
        # 관심사 인코딩 (10차원)
        interest_vector = [1.0 if interest in user.interests else 0.0 for interest in self.common_interests]
        features.extend(interest_vector)
        
        # 성격 키워드 인코딩 (10차원)
        personality_vector = [1.0 if trait in user.personality_keywords else 0.0 for trait in self.personality_traits]
        features.extend(personality_vector)
        
        # 학과 인코딩 (간단화)
        dept_score = 1.0 if user.department else 0.0
        features.append(dept_score)
        
        # 나이 정규화
        age_score = 0.5
        if user.birth_year:
            age = 2024 - user.birth_year
            age_score = min(max((age - 18) / 10, 0), 1)
        features.append(age_score)
        
        # 키 정규화
        height_score = 0.5
        if user.height:
            height_score = min(max((user.height - 150) / 50, 0), 1)
        features.append(height_score)
        
        return np.array(features)

    def calculate_compatibility(self, user1: UserProfile, user2: UserProfile) -> Dict[str, float]:
        """두 사용자 간 호환성 계산"""
        try:
            # 벡터 기반 유사도
            user1_vector = self.encode_user_features(user1)
            user2_vector = self.encode_user_features(user2)
            
            # 코사인 유사도 계산
            cosine_sim = cosine_similarity([user1_vector], [user2_vector])[0][0]
            
            # 세부 점수 계산
            detailed_scores = {
                'mbti': self._calculate_mbti_score(user1.mbti, user2.mbti),
                'interests': self._calculate_interest_score(user1.interests, user2.interests),
                'personality': self._calculate_personality_score(user1.personality_keywords, user2.personality_keywords),
                'department': self._calculate_department_score(user1.department, user2.department),
                'age': self._calculate_age_score(user1.birth_year, user2.birth_year),
                'height': self._calculate_height_score(user1.height, user2.height),
                'cosine_similarity': float(cosine_sim),
                'total': float(cosine_sim * 0.7 + self._calculate_rule_based_score(user1, user2) * 0.3)
            }
            
            return detailed_scores
            
        except Exception as e:
            logger.error(f"호환성 계산 실패: {e}")
            return {'total': 0.5, 'error': str(e)}

    def _calculate_mbti_score(self, mbti1: str, mbti2: str) -> float:
        """MBTI 호환성 점수"""
        if not mbti1 or not mbti2:
            return 0.5
        
        if mbti1 == mbti2:
            return 0.8
        
        if mbti1 in self.mbti_compatibility and mbti2 in self.mbti_compatibility[mbti1]:
            return 0.9
        
        return 0.4

    def _calculate_interest_score(self, interests1: List[str], interests2: List[str]) -> float:
        """관심사 유사도 점수"""
        if not interests1 or not interests2:
            return 0.5
        
        set1, set2 = set(interests1), set(interests2)
        intersection = len(set1 & set2)
        union = len(set1 | set2)
        
        return 0.3 + (intersection / union * 0.7) if union > 0 else 0.5

    def _calculate_personality_score(self, personality1: List[str], personality2: List[str]) -> float:
        """성격 유사도 점수"""
        if not personality1 or not personality2:
            return 0.5
        
        set1, set2 = set(personality1), set(personality2)
        intersection = len(set1 & set2)
        union = len(set1 | set2)
        
        return 0.4 + (intersection / union * 0.6) if union > 0 else 0.5

    def _calculate_department_score(self, dept1: str, dept2: str) -> float:
        """학과 호환성 점수"""
        if not dept1 or not dept2:
            return 0.5
        
        if dept1 == dept2:
            return 1.0
        
        return 0.4

    def _calculate_age_score(self, birth_year1: int, birth_year2: int) -> float:
        """나이 호환성 점수"""
        if not birth_year1 or not birth_year2:
            return 0.5
        
        age_diff = abs(birth_year1 - birth_year2)
        
        if age_diff == 0: return 1.0
        if age_diff == 1: return 0.9
        if age_diff <= 2: return 0.8
        if age_diff <= 3: return 0.7
        if age_diff <= 5: return 0.5
        
        return 0.3

    def _calculate_height_score(self, height1: int, height2: int) -> float:
        """키 호환성 점수"""
        if not height1 or not height2:
            return 0.5
        
        height_diff = abs(height1 - height2)
        
        if height_diff <= 3: return 1.0
        if height_diff <= 5: return 0.9
        if height_diff <= 10: return 0.8
        if height_diff <= 15: return 0.6
        
        return 0.4

    def _calculate_rule_based_score(self, user1: UserProfile, user2: UserProfile) -> float:
        """규칙 기반 종합 점수"""
        scores = [
            self._calculate_mbti_score(user1.mbti, user2.mbti) * 0.3,
            self._calculate_interest_score(user1.interests, user2.interests) * 0.25,
            self._calculate_personality_score(user1.personality_keywords, user2.personality_keywords) * 0.25,
            self._calculate_department_score(user1.department, user2.department) * 0.1,
            self._calculate_age_score(user1.birth_year, user2.birth_year) * 0.05,
            self._calculate_height_score(user1.height, user2.height) * 0.05
        ]
        
        return sum(scores)

# 전역 AI 서비스 인스턴스
ai_service = SimpleAIMatchingService()

@app.get("/")
def root():
    """서비스 상태 확인"""
    return {
        "service": "UniMeet Simple AI Matching Service",
        "status": "running",
        "version": "1.0.0",
        "description": "scikit-learn 기반 안정적인 매칭 시스템"
    }

@app.get("/test")
def test():
    """간단한 테스트 엔드포인트"""
    return {"message": "Simple AI Service is running!", "status": "OK"}

@app.post("/match", response_model=List[MatchResult])
def find_matches(request: MatchRequest):
    """AI 매칭 수행"""
    try:
        results = []
        
        for candidate in request.candidate_users:
            # 호환성 계산
            compatibility_data = ai_service.calculate_compatibility(
                request.target_user, candidate
            )
            
            result = MatchResult(
                user_id=candidate.user_id,
                compatibility_score=compatibility_data.get('total', 0.0),
                detailed_scores=compatibility_data
            )
            results.append(result)
        
        # 호환성 점수로 정렬
        results.sort(key=lambda x: x.compatibility_score, reverse=True)
        
        # 상위 K개 반환
        return results[:request.top_k]
        
    except Exception as e:
        logger.error(f"매칭 처리 실패: {e}")
        raise HTTPException(status_code=500, detail=f"매칭 처리 중 오류 발생: {str(e)}")

@app.post("/compatibility")
def calculate_compatibility(user1: UserProfile, user2: UserProfile):
    """두 사용자 간 호환성 계산"""
    try:
        compatibility_data = ai_service.calculate_compatibility(user1, user2)
        return {
            "user1_id": user1.user_id,
            "user2_id": user2.user_id,
            "compatibility_score": compatibility_data.get('total', 0.0),
            "detailed_scores": compatibility_data
        }
    except Exception as e:
        logger.error(f"호환성 계산 실패: {e}")
        raise HTTPException(status_code=500, detail=f"호환성 계산 중 오류 발생: {str(e)}")

# 시연용 간단한 매칭 API
@app.post("/recommend")
def recommend(data: dict):
    """간단한 추천 API (기존 main.py 호환)"""
    try:
        user_vector = np.array(data.get('user_vector', []))
        candidates = np.array(data.get('candidates', []))
        
        if len(user_vector) == 0 or len(candidates) == 0:
            return {"error": "Invalid input data"}
        
        # 코사인 유사도 계산
        sims = cosine_similarity([user_vector], candidates)[0]
        ranks = sims.argsort()[::-1]
        
        return {
            "ranking": ranks.tolist(),
            "scores": sims.tolist(),
            "message": "Simple AI matching completed"
        }
        
    except Exception as e:
        logger.error(f"추천 처리 실패: {e}")
        return {"error": str(e)}

if __name__ == "__main__":
    import uvicorn
    logger.info("🚀 UniMeet Simple AI 서비스 시작")
    uvicorn.run(app, host="0.0.0.0", port=8001)