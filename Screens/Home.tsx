import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import Header from '../navigation/Header';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';

// 참가자 타입 정의
type Participant = {
  id: number;
  name: string;
  gender: '남' | '여';
};

// 미팅방 타입 정의
type MeetingRoom = {
  id: number;
  participants: Participant[];
};

const Home: React.FC = () => {
  const [meetingRooms, setMeetingRooms] = useState<MeetingRoom[]>([
    {
      id: 1,
      participants: [
        { id: 1, name: "컴퓨터공학과", gender: "남" },
        { id: 2, name: "메카트로닉스공학과", gender: "남" },
        { id: 3, name: "유아교육과", gender: "여" },
        { id: 4, name: "간호학과", gender: "여" },
      ],
    },
    {
      id: 2,
      participants: [
        { id: 5, name: "남자3", gender: "남" },
        { id: 6, name: "", gender: "남" },
        { id: 7, name: "", gender: "남" },
        { id: 8, name: "여자3", gender: "여" },
        { id: 9, name: "여자4", gender: "여" },
        { id: 10, name: "", gender: "여" },
      ],
    },
    {
      id: 3,
      participants: [
        { id: 1, name: "생명공학과", gender: "남" },
        { id: 2, name: "바이오메디컬공학과", gender: "여" },
      ]
    },
  ]);

  const handleNotificationPress = () => {
    alert('홈 화면에서 알림을 눌렀습니다!');
  };

  return (
    <LinearGradient
        colors={['#FFB1E8', '#EC75FF', '#947CFF', '#F0F0E9', '#F0F0E9']}
        locations={[0, 0.28, 0.54, 0.9, 1]}
        start={{ x: 0, y: 0 }}
        end={{ x: 0, y: 0.5 }}
        style={styles.container}
        >

      <Header title="UniMeet" onNotificationPress={handleNotificationPress} />

      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.comment}>
          <Ionicons name="rocket-outline" size={12} color="#3D3D3D" style={styles.icon} />
          <Text style={styles.commentText}>여기는 개발자 코멘트를 적는 곳.</Text>
        </View>

        <View style={styles.guideBox}>
          <Text style={styles.guideBoxText}>여기는 이용 가이드 / 이미지를 넣는 곳.</Text>
        </View>

        <View style={styles.meetingContainer}>
          <Text style={styles.text1}>미팅</Text>
          <Ionicons name="help-circle-outline" size={20} color="#3D3D3D" style={styles.icon} />
        </View>

        {meetingRooms.map((room) => (
          <View key={room.id} style={styles.meetingCard}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardTitle}>미팅 방 {room.id}</Text>
              <Ionicons name="people-outline" size={24} color="#3D3D3D" />
            </View>
            <View style={styles.participantsContainer}>
              {room.id === 3 ? (
                // 혼성방: 모든 참가자를 주황색으로 표시
                <View style={styles.group}>
                  {room.participants.map((participant) => (
                    <View key={participant.id} style={styles.participantRow}>
                      <Ionicons name="person" size={15} style={styles.mixed} />
                      <Text style={[styles.participantText, styles.mixed]}>{participant.name}</Text>
                    </View>
                  ))}
                </View>
              ) : (
                <>
                  {/* 남자 그룹 */}
                  <View style={styles.group}>
                    {room.participants
                      .filter((participant) => participant.gender === "남")
                      .map((participant) => (
                        <View key={participant.id} style={styles.participantRow}>
                          <Ionicons name="person" size={15} style={styles.male} />
                          <Text style={[styles.participantText, styles.male]}>{participant.name}</Text>
                        </View>
                      ))}
                  </View>
                  {/* 세로선 */}
                  <View style={styles.divider}></View>
                  {/* 여자 그룹 */}
                  <View style={styles.group}>
                    {room.participants
                      .filter((participant) => participant.gender === "여")
                      .map((participant) => (
                        <View key={participant.id} style={styles.participantRow}>
                          <Ionicons name="person" size={15} style={styles.female} />
                          <Text style={[styles.participantText, styles.female]}>{participant.name}</Text>
                        </View>
                      ))}
                  </View>
                </>
              )}
            </View>
          </View>
        ))}
      </ScrollView>



    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    paddingTop: 10,
    paddingHorizontal: 15,
    alignItems: 'center',
  },
  comment: {
    width: '100%',
    height: 30,
    backgroundColor: '#D1D0D0',
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
    flexDirection: 'row',
  },
  commentText: {
    fontSize: 11,
    color: '#3D3D3D',
  },
  guideBox: {
    width: '100%',
    height: 100,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#D1D0D0',
    marginBottom: 40,
  },
  guideBoxText: {
    textAlign: 'center',
    color: '#3D3D3D',
  },
  text1: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  meetingContainer: {
    width: '100%',
    flexDirection: 'row',
    marginBottom: 5,
  },
  icon: {
    marginRight: 5,
  },
  meetingCard: {
    width: '100%',
    padding: 15,
    borderRadius: 10,
    backgroundColor: '#FFFFFF',
    marginBottom: 15,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 5,
    elevation: 3,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  participantsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    paddingHorizontal: 10,
  },
  group: {
    flex: 1,
  },
  participantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
  },
  male: {
    color: '#1976d2',
    marginRight: 5,
  },
  female: {
    color: '#d81b60',
    marginRight: 5,
  },
  mixed: {
    color: '#FF9800', // 주황색
    marginRight: 5,
  },
  participantText: {
    fontSize: 14,
  },
  divider: {
    width: 1,
    backgroundColor: '#ccc',
    marginHorizontal: 10,
    height: '100%',
  },
});

export default Home;
