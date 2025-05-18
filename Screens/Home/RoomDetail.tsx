import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { RouteProp, useRoute, useNavigation } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import type { RootStackParamList } from '../../navigation/types';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

// 참가자 정보 카드 컴포넌트
const ParticipantInfo: React.FC<{ p: any; color: string }> = ({ p, color }) => (
  <View style={styles.participantCard}>
    <Ionicons
      name="person"
      size={12}
      color={color}
      style={styles.cardIcon}
    />
    <View style={styles.departmentBox}>
      <Text style={[styles.participantDepartment, { color }]} numberOfLines={1} ellipsizeMode="tail">
        {p.department || ''}
      </Text>
    </View>
    {/* 정보 박스 */}
    <View style={styles.detailsBox}>
      <View style={styles.detailItem}>
        <Text style={styles.detailText}>나이: {p.age || ''}</Text>
        <View style={styles.line} />
      </View>
      <View style={styles.detailItem}>
        <Text style={styles.detailText}>학번: {p.studentId || ''}</Text>
        <View style={styles.line} />
      </View>
      <View style={styles.detailItem}>
        <Text style={styles.detailText}>MBTI: {p.mbti || ''}</Text>
        <View style={styles.line} />
      </View>
      <Text style={styles.detailText}>관심사: {p.interests?.join(', ') || ''}</Text>
    </View>
  </View>
);

// 3개씩 끊어서 2차원 배열로 변환
function chunkArray(arr: any[], size: number) {
  const result = [];
  for (let i = 0; i < arr.length; i += size) {
    result.push(arr.slice(i, i + size));
  }
  return result;
}

const RoomDetail: React.FC = () => {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const route = useRoute<RouteProp<RootStackParamList, 'RoomDetail'>>();
  const { room } = route.params;

  const maleList = room.participants.filter(p => p.gender === '남');
  const femaleList = room.participants.filter(p => p.gender === '여');
  const isMixed = room.type === 'mixed';

  // 색상 결정 함수
  const getColor = (p: any) => {
    if (isMixed) return '#FF9800'; // 혼성방은 주황
    if (p.gender === '여') return '#FF62D5'; // 여자 핑크
    return '#6846FF'; // 남자 보라
  };

  return (
    <LinearGradient
      colors={['#FF87DD', '#B092FF', '#DBD6EC', '#F0F0E9']}
      locations={[0, 0.43, 0.71, 0.93]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0, y: 0.35 }}
      style={styles.container}
    >
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.sideButton}>
          <Ionicons name="arrow-back" size={25} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.title}>Meeting Room</Text>
        <View style={{ width: 36 }} />
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.comment}>
          <Ionicons name="rocket-outline" size={12} color="#3D3D3D" style={styles.icon} />
          <Text style={styles.commentText}>모든 멤버가 참가 상태이면 정식 채팅방이 시작됩니다.</Text>
        </View>

        <View>
          <Text style={styles.mainTitle}>{room.title}</Text>
        </View>

        {isMixed ? (
          <View style={styles.participantGroupBox}>
            {chunkArray(room.participants, 3).map((row, rowIdx) => (
              <View key={rowIdx} style={styles.groupRow}>
                {row.map(p => (
                  <View key={p.id} style={styles.participantCardWrapper}>
                    <ParticipantInfo p={p} color={getColor(p)} />
                  </View>
                ))}
              </View>
            ))}
          </View>
        ) : (
          <>
            <Text style={styles.groupLabel}>남자그룹</Text>
            <View style={styles.participantGroupBox}>
              {chunkArray(maleList, 3).map((row, rowIdx) => (
                <View key={rowIdx} style={styles.groupRow}>
                  {row.map(p => (
                    <View key={p.id} style={styles.participantCardWrapper}>
                      <ParticipantInfo p={p} color={getColor(p)} />
                    </View>
                  ))}
                </View>
              ))}
            </View>
            <Text style={styles.groupLabel}>여자그룹</Text>
            <View style={styles.participantGroupBox}>
              {chunkArray(femaleList, 3).map((row, rowIdx) => (
                <View key={rowIdx} style={styles.groupRow}>
                  {row.map(p => (
                    <View key={p.id} style={styles.participantCardWrapper}>
                      <ParticipantInfo p={p} color={getColor(p)} />
                    </View>
                  ))}
                </View>
              ))}
            </View>
          </>
        )}
      </ScrollView>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingTop: 70,
    paddingBottom: 18,
    paddingHorizontal: 18,
    justifyContent: 'space-between',
  },
  title: {
    fontSize: 18,
    color: '#fff',
    fontWeight: '600',
    textAlign: 'center',
    textShadowColor: '#000',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 4,
    elevation: 4,
  },
  content: {
    paddingTop: 10,
    paddingHorizontal: 15,
  },
  mainTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 18,
    marginTop: 18,
  },
  groupLabel: {
    fontSize: 12,
    fontWeight: 'bold',
    marginTop: 15,
    marginBottom: 15,
    marginLeft: 6,
  },
  participantGroupBox: {
    marginBottom: 16,
    alignItems: 'flex-start',
    flex: 1,
  },
  groupRow: {
    flexDirection: 'row',
    flexWrap: 'nowrap',
    marginBottom: 12,
  },
  participantCardWrapper: {
    marginRight: 12,
    marginBottom: 0,
  },
  sideButton: {
    width: 40,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 2,
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 3 },
    shadowOpacity: 0.5,
    shadowRadius: 4,
    elevation: 5,
  },
  comment: {
    width: '100%',
    height: 28,
    backgroundColor: '#FFFFFF',
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
    flexDirection: 'row',
  },
  commentText: {
    fontSize: 11,
    color: '#3D3D3D'
  },
  icon: {
    marginRight: 5,
    marginBottom: 2,
    color: '#3D3D3D'
  },
  participantCard: {
    width: 112,
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
    position: 'relative',
    alignItems: 'center',
    justifyContent: 'flex-start',
  },
  cardIcon: {
    position: 'absolute',
    top: 8,
    left: 8,
    zIndex: 2,
  },
  departmentBox: {
    marginTop: 10,
  },
  participantDepartment: {
    fontSize: 10,
    fontWeight: '500',
    textAlign: 'center',
  },
  detailsBox: {
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 6,
    padding: 8,
    marginTop: 8,
    backgroundColor: '#FAF8F6'
  },
  detailItem: {
    marginBottom: 6,
  },
  detailText: {
    fontSize: 10,
    color: '#444',
  },
  line: {
    height: 1,
    backgroundColor: '#D9D9D9',
    marginVertical: 5,
  },
});

export default RoomDetail;
