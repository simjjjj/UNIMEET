"""
UniMeet AI 매칭 서비스
TensorFlow 기반 딥러닝 매칭 시스템
"""

import tensorflow as tf
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.model_selection import train_test_split
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Optional
import json
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="UniMeet AI Matching Service", version="1.0.0")

class UserProfile(BaseModel):
    """사용자 프로필 데이터 모델"""
    user_id: str
    mbti: Optional[str] = None
    interests: List[str] = []
    personality_keywords: List[str] = []
    department: Optional[str] = None
    birth_year: Optional[int] = None
    height: Optional[int] = None
    gender: Optional[str] = None

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

class AIMatchingService:
    """AI 매칭 서비스 클래스"""
    
    def __init__(self):
        self.model = None
        self.scaler = StandardScaler()
        self.label_encoders = {}
        self.feature_dim = 50  # 특성 벡터 차원
        self.is_trained = False
        
        # 특성 가중치 (하드코딩에서 학습 가능한 가중치로)
        self.feature_weights = {
            'mbti': 0.25,
            'interests': 0.20,
            'personality': 0.20,
            'department': 0.15,
            'age': 0.10,
            'height': 0.05,
            'gender': 0.05
        }
        
        self._build_model()
        logger.info("AI 매칭 서비스 초기화 완료")

    def _build_model(self):
        """TensorFlow 딥러닝 모델 구축"""
        try:
            # Neural Collaborative Filtering 모델
            user_input = tf.keras.layers.Input(shape=(self.feature_dim,), name='user_features')
            candidate_input = tf.keras.layers.Input(shape=(self.feature_dim,), name='candidate_features')
            
            # 사용자 특성 임베딩
            user_dense = tf.keras.layers.Dense(128, activation='relu')(user_input)
            user_dense = tf.keras.layers.Dropout(0.3)(user_dense)
            user_dense = tf.keras.layers.Dense(64, activation='relu')(user_dense)
            
            # 후보자 특성 임베딩
            candidate_dense = tf.keras.layers.Dense(128, activation='relu')(candidate_input)
            candidate_dense = tf.keras.layers.Dropout(0.3)(candidate_dense)
            candidate_dense = tf.keras.layers.Dense(64, activation='relu')(candidate_dense)
            
            # 특성 결합 및 호환성 계산
            concat = tf.keras.layers.Concatenate()([user_dense, candidate_dense])
            
            # 딥러닝 레이어
            dense1 = tf.keras.layers.Dense(128, activation='relu')(concat)
            dense1 = tf.keras.layers.Dropout(0.4)(dense1)
            dense2 = tf.keras.layers.Dense(64, activation='relu')(dense1)
            dense2 = tf.keras.layers.Dropout(0.3)(dense2)
            dense3 = tf.keras.layers.Dense(32, activation='relu')(dense2)
            
            # 출력: 호환성 점수 (0-1)
            output = tf.keras.layers.Dense(1, activation='sigmoid', name='compatibility')(dense3)
            
            # 모델 컴파일
            self.model = tf.keras.Model(
                inputs=[user_input, candidate_input], 
                outputs=output
            )
            
            self.model.compile(
                optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
                loss='binary_crossentropy',
                metrics=['mae', 'mse']
            )
            
            logger.info("TensorFlow 모델 구축 완료")
            logger.info(f"모델 구조:\n{self.model.summary()}")
            
        except Exception as e:
            logger.error(f"모델 구축 실패: {e}")
            raise

    def _encode_user_features(self, user: UserProfile) -> np.ndarray:
        """사용자 특성을 벡터로 인코딩"""
        features = np.zeros(self.feature_dim)
        idx = 0
        
        try:
            # MBTI 인코딩 (16차원 원-핫)
            mbti_types = ['INTJ', 'INTP', 'ENTJ', 'ENTP', 'INFJ', 'INFP', 'ENFJ', 'ENFP',
                         'ISTJ', 'ISFJ', 'ESTJ', 'ESFJ', 'ISTP', 'ISFP', 'ESTP', 'ESFP']
            if user.mbti and user.mbti in mbti_types:
                features[idx + mbti_types.index(user.mbti)] = 1.0
            idx += 16
            
            # 관심사 인코딩 (10차원)
            common_interests = ['독서', '영화감상', '운동', '음악', '여행', '게임', '요리', '사진', '춤', '그림']
            for i, interest in enumerate(common_interests):
                if interest in user.interests:
                    features[idx + i] = 1.0
            idx += 10
            
            # 성격 키워드 인코딩 (10차원)
            personality_traits = ['외향적', '내향적', '논리적', '감정적', '계획적', '즉흥적', '활발한', '차분한', '창의적', '현실적']
            for i, trait in enumerate(personality_traits):
                if trait in user.personality_keywords:
                    features[idx + i] = 1.0
            idx += 10
            
            # 학과 인코딩 (8차원)
            departments = ['공학계열', '인문계열', '사회계열', '자연계열', '예체능계열', '의료계열', '교육계열', '기타']
            dept_mapping = {
                '컴퓨터공학과': '공학계열', '전자공학과': '공학계열', '기계공학과': '공학계열',
                '국어국문학과': '인문계열', '영어영문학과': '인문계열', '사학과': '인문계열',
                '경영학과': '사회계열', '경제학과': '사회계열', '정치외교학과': '사회계열',
                '수학과': '자연계열', '물리학과': '자연계열', '화학과': '자연계열',
                '미술학과': '예체능계열', '음악학과': '예체능계열', '체육학과': '예체능계열',
                '의학과': '의료계열', '간호학과': '의료계열', '약학과': '의료계열'
            }
            if user.department and user.department in dept_mapping:
                dept_category = dept_mapping[user.department]
                if dept_category in departments:
                    features[idx + departments.index(dept_category)] = 1.0
            idx += 8
            
            # 나이 정규화 (1차원)
            if user.birth_year:
                age = 2024 - user.birth_year
                features[idx] = min(max((age - 18) / 10, 0), 1)  # 18-28세를 0-1로 정규화
            idx += 1
            
            # 키 정규화 (1차원)
            if user.height:
                features[idx] = min(max((user.height - 150) / 50, 0), 1)  # 150-200cm를 0-1로 정규화
            idx += 1
            
            # 성별 인코딩 (2차원)
            if user.gender == 'M':
                features[idx] = 1.0
            elif user.gender == 'F':
                features[idx + 1] = 1.0
            idx += 2
            
            # 나머지 차원은 0으로 패딩
            return features
            
        except Exception as e:
            logger.error(f"특성 인코딩 실패: {e}")
            return np.zeros(self.feature_dim)

    def calculate_compatibility(self, user1: UserProfile, user2: UserProfile) -> Dict[str, float]:
        """두 사용자 간 호환성 계산"""
        try:
            # 특성 벡터 생성
            user1_features = self._encode_user_features(user1)
            user2_features = self._encode_user_features(user2)
            
            # 모델이 훈련되지 않은 경우 규칙 기반 계산
            if not self.is_trained:
                return self._rule_based_compatibility(user1, user2)
            
            # AI 모델 예측
            user1_features = user1_features.reshape(1, -1)
            user2_features = user2_features.reshape(1, -1)
            
            compatibility_score = self.model.predict([user1_features, user2_features])[0][0]
            
            # 세부 점수 계산
            detailed_scores = self._calculate_detailed_scores(user1, user2)
            detailed_scores['ai_total'] = float(compatibility_score)
            
            return detailed_scores
            
        except Exception as e:
            logger.error(f"호환성 계산 실패: {e}")
            return self._rule_based_compatibility(user1, user2)

    def _rule_based_compatibility(self, user1: UserProfile, user2: UserProfile) -> Dict[str, float]:
        """규칙 기반 호환성 계산 (AI 모델 백업)"""
        scores = {}
        
        # MBTI 호환성
        scores['mbti'] = self._calculate_mbti_score(user1.mbti, user2.mbti)
        
        # 관심사 유사도
        scores['interests'] = self._calculate_interest_score(user1.interests, user2.interests)
        
        # 성격 유사도
        scores['personality'] = self._calculate_personality_score(user1.personality_keywords, user2.personality_keywords)
        
        # 학과 호환성
        scores['department'] = self._calculate_department_score(user1.department, user2.department)
        
        # 나이 호환성
        scores['age'] = self._calculate_age_score(user1.birth_year, user2.birth_year)
        
        # 키 호환성
        scores['height'] = self._calculate_height_score(user1.height, user2.height)
        
        # 전체 점수 (가중 평균)
        total_score = sum(scores[key] * self.feature_weights.get(key, 0) for key in scores)
        scores['total'] = total_score
        
        return scores

    def _calculate_mbti_score(self, mbti1: str, mbti2: str) -> float:
        """MBTI 호환성 점수"""
        if not mbti1 or not mbti2:
            return 0.5
        
        if mbti1 == mbti2:
            return 0.8
        
        # 간단한 MBTI 호환성 규칙
        compatibility_map = {
            'INTJ': ['ENFP', 'ENTP'], 'INTP': ['ENFJ', 'ENTJ'],
            'ENTJ': ['INFP', 'INTP'], 'ENTP': ['INFJ', 'INTJ'],
            'INFJ': ['ENFP', 'ENTP'], 'INFP': ['ENFJ', 'ENTJ'],
            'ENFJ': ['INFP', 'INTP'], 'ENFP': ['INTJ', 'INFJ']
        }
        
        if mbti1 in compatibility_map and mbti2 in compatibility_map[mbti1]:
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
        
        # 계열별 그룹핑 (간단화)
        engineering = ['컴퓨터공학과', '전자공학과', '기계공학과']
        humanities = ['국어국문학과', '영어영문학과', '사학과']
        social = ['경영학과', '경제학과', '정치외교학과']
        
        for group in [engineering, humanities, social]:
            if dept1 in group and dept2 in group:
                return 0.8
        
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

    def _calculate_detailed_scores(self, user1: UserProfile, user2: UserProfile) -> Dict[str, float]:
        """세부 점수 계산"""
        return {
            'mbti': self._calculate_mbti_score(user1.mbti, user2.mbti),
            'interests': self._calculate_interest_score(user1.interests, user2.interests),
            'personality': self._calculate_personality_score(user1.personality_keywords, user2.personality_keywords),
            'department': self._calculate_department_score(user1.department, user2.department),
            'age': self._calculate_age_score(user1.birth_year, user2.birth_year),
            'height': self._calculate_height_score(user1.height, user2.height)
        }

# 전역 AI 서비스 인스턴스
ai_service = AIMatchingService()

@app.get("/")
def root():
    """서비스 상태 확인"""
    return {
        "service": "UniMeet AI Matching Service",
        "status": "running",
        "model_trained": ai_service.is_trained,
        "version": "1.0.0"
    }

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

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)