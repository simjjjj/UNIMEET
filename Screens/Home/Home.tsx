import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import Header from '../../navigation/Header';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';

type Participant = {
  id: number;
  name: string;
  gender: '남' | '여';
};

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
        { id: 2, name: "", gender: "여" },
        { id: 3, name: "컴퓨터공학과", gender: '남'},
        { id: 4, name: '스포츠건강학과', gender: '여'},
        { id: 5, name: '바이오메디컬공학과', gender: '여'},
        { id: 6, name: '', gender: '남'},
      ]
    },
  ]);

  const handleNotificationPress = () => {
    alert('홈 화면에서 알림을 눌렀습니다!');
  };

  const handleCreateRoom = () => {
    alert('방 만들기 기능 준비 중!');
  };

  const handleMeetingInfo = () => {
    alert('설명하는 기능 준비중');
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
          <TouchableOpacity onPress={handleMeetingInfo}>
            <Ionicons name="help-circle-outline" size={20} style={styles.icon} />
          </TouchableOpacity>
          <View style={{ flex: 1 }} />
          <TouchableOpacity style={styles.createRoomBtn} onPress={handleCreateRoom}>
            <Ionicons name="add-circle-outline" size={15} style={{ marginRight: 2 }} />
            <Text style={styles.createRoomBtnText}>방만들기</Text>
          </TouchableOpacity>
        </View>

        {meetingRooms.map((room) => (
          <View key={room.id} style={styles.meetingCard}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardTitle}>미팅 방 {room.id}</Text>
            </View>
            <View style={styles.participantsContainer}>
              {/* 혼성 그룹 */}
              {room.id === 3 ? (
                <View style={styles.mixedBox}>
                  <Text style={styles.mixedLabel}>혼성</Text>
                  <View style={styles.mixedParticipantsWrap}>
                    {room.participants.map((participant) => (
                      <View key={participant.id} style={styles.mixedParticipantRow}>
                        <Ionicons name="person" size={15} style={styles.mixedIcon} />
                        <Text
                          style={[
                            styles.participantText,
                            styles.mixedIcon,
                            participant.name === "" && styles.noName,
                          ]}
                        >
                          {participant.name === "" ? "없음" : participant.name}
                        </Text>
                      </View>
                    ))}
                  </View>
                </View>
              ) : (
                <View style={styles.genderRowWrap}>
                  {/* 남자 그룹 */}
                  <View style={styles.genderBoxMale}>
                    <Text style={styles.genderLabelMale}>남</Text>
                    <View style={styles.participantsWrap}>
                      {room.participants
                        .filter((participant) => participant.gender === "남")
                        .map((participant) => (
                          <View key={participant.id} style={styles.participantRow}>
                            <Ionicons name="person" size={12} style={styles.male} />
                            <Text
                              style={[
                                styles.participantText,
                                styles.male,
                                participant.name === "" && styles.noName,
                              ]}
                            >
                              {participant.name === "" ? "없음" : participant.name}
                            </Text>
                          </View>
                        ))}
                    </View>
                  </View>
                  {/* 여자 그룹 */}
                  <View style={styles.genderBoxFemale}>
                    <Text style={styles.genderLabelFemale}>여</Text>
                    <View style={styles.participantsWrap}>
                      {room.participants
                        .filter((participant) => participant.gender === "여")
                        .map((participant) => (
                          <View key={participant.id} style={styles.participantRow}>
                            <Ionicons name="person" size={12} style={styles.female} />
                            <Text
                              style={[
                                styles.participantText,
                                styles.female,
                                participant.name === "" && styles.noName,
                              ]}
                            >
                              {participant.name === "" ? "없음" : participant.name}
                            </Text>
                          </View>
                        ))}
                    </View>
                  </View>
                </View>
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
    marginBottom: 30,
  },
  guideBoxText: {
    textAlign: 'center',
    color: '#3D3D3D',
  },
  text1: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 2,
    marginLeft: 5,
  },
  meetingContainer: {
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 5,
  },
  icon: {
    marginRight: 5,
    marginBottom: 2,
    color: '#3D3D3D',
  },
  createRoomBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 5,
    paddingHorizontal: 8,
  },
  createRoomBtnText: {
    fontWeight: '600',
    fontSize: 12,
  },
  meetingCard: {
    width: '100%',
    paddingTop: 15,
    paddingBottom: 15,
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
    marginLeft: 13,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  participantsContainer: {
    flexDirection: 'column',
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    paddingHorizontal: 10,
    width: '100%',
  },
  // 남/여 그룹 한 줄에
  genderRowWrap: {
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  genderBoxMale: {
    flex: 1,
    backgroundColor: '#F0F0F0',
    borderRadius: 10,
    flexDirection: 'row',
    alignItems: 'center',
    padding: 10,
    marginRight: 5,
    minHeight: 60,
    // iOS
    shadowColor: '#000',
    shadowOffset: { width: 1, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 3,

    // Android
    elevation: 4,
  },
  genderBoxFemale: {
    flex: 1,
    backgroundColor: '#F0F0F0',
    borderRadius: 10,
    flexDirection: 'row',
    alignItems: 'center',
    padding: 10,
    marginLeft: 5,
    minHeight: 60,
    shadowColor: '#000',
    shadowOffset: { width: 1, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 4,
  },
  genderLabelMale: {
    fontWeight: '500',
    color: '#1976d2',
    fontSize: 12,
    marginRight: 8,
    minWidth: 20,
    textAlignVertical: 'center',
  },
  genderLabelFemale: {
    fontWeight: '500',
    color: '#d81b60',
    fontSize: 12,
    marginRight: 8,
    minWidth: 20,
    textAlignVertical: 'center',
  },
  participantsWrap: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
  },
  participantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
    minWidth: 70,
  },
  mixedBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F0F0F0',
    borderRadius: 10,
    padding: 10,
    minHeight: 60,
    width: '100%',
    shadowColor: '#000',
    shadowOffset: { width: 1, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 4,
  },
  mixedLabel: {
    fontWeight: '500',
    color: '#FF9800',
    fontSize: 12,
    minWidth: 28,
    textAlignVertical: 'center',
  },
  mixedParticipantsWrap: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
  },
  mixedParticipantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 7,
    minWidth: 120,
    marginLeft: 25,
  },
  male: {
    color: '#1976d2',
    marginRight: 5,
  },
  female: {
    color: '#d81b60',
    marginRight: 5,
  },
  mixedIcon: {
    color: '#FF9800',
    marginRight: 5,
  },
  participantText: {
    fontSize: 12,
  },
  noName: {
    color: '#BDBDBD',
    fontStyle: 'italic',
  },
});

export default Home;
