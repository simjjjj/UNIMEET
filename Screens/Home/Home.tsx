import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Modal, TextInput, Button } from 'react-native';
import Header from '../../navigation/Header';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';

// 타입 정의
type Gender = '남' | '여';

interface Participant {
  id: number;
  name: string;
  gender: Gender;
}

interface MeetingRoom {
  id: number;
  title: string;
  participants: Participant[];
  type?: 'pair' | 'mixed'; // 혼성방 여부
}

interface RoomPreset {
  label: string;
  male: number;
  female: number;
  type: 'pair' | 'mixed';
}

const ROOM_PRESETS: RoomPreset[] = [
  { label: '1:1', male: 1, female: 1, type: 'pair' },
  { label: '2:2', male: 2, female: 2, type: 'pair' },
  { label: '3:3', male: 3, female: 3, type: 'pair' },
  { label: '혼성 4인', male: 2, female: 2, type: 'mixed' },
  { label: '혼성 6인', male: 3, female: 3, type: 'mixed' },
];

const Home: React.FC = () => {
  const [meetingRooms, setMeetingRooms] = useState<MeetingRoom[]>([
    {
      id: 1,
      title: '기본 2:2',
      participants: [
        { id: 1, name: "컴퓨터공학과", gender: "남" },
        { id: 2, name: "메카트로닉스공학과", gender: "남" },
        { id: 3, name: "유아교육과", gender: "여" },
        { id: 4, name: "간호학과", gender: "여" },
      ],
      type: 'pair',
    },
    {
      id: 2,
      title: '3:3 방',
      participants: [
        { id: 5, name: "남자3", gender: "남" },
        { id: 6, name: "남자4", gender: "남" },
        { id: 7, name: "남자5", gender: "남" },
        { id: 8, name: "여자3", gender: "여" },
        { id: 9, name: "여자4", gender: "여" },
        { id: 10, name: "여자6", gender: "여" },
      ],
      type: 'pair',
    },
    {
      id: 3,
      title: '혼성방',
      participants: [
        { id: 1, name: "생명공학과", gender: "남" },
        { id: 2, name: "스포츠건강학과", gender: "여" },
        { id: 3, name: "컴퓨터공학과", gender: '남' },
        { id: 4, name: '', gender: '여' },
        { id: 5, name: '바이오메디컬공학과', gender: '여' },
        { id: 6, name: '', gender: '남' },
      ],
      type: 'mixed',
    },
  ]);

  // 모달 상태 및 입력값
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [roomTitle, setRoomTitle] = useState<string>('');
  const [selectedPreset, setSelectedPreset] = useState<RoomPreset>(ROOM_PRESETS[0]);

  // 인원수 표시
  const getPeopleLabel = (room: MeetingRoom) => {
    const maleTotal :number = room.participants.filter(p => p.gender === "남").length;
    const femaleTotal :number = room.participants.filter(p => p.gender === "여").length;
    if (room.type === 'mixed') {
      return `${room.participants.length}인`;
    }
    const maleCountFilled :number = room.participants.filter(p => p.gender === "남" && p.name !== "").length;
    const femaleCountFilled :number = room.participants.filter(p => p.gender === "여" && p.name !== "").length;
    return `${maleCountFilled}:${femaleCountFilled}`;
  };

  // 방이 다 찼는지(name이 모두 채워졌는지)
  const isRoomFull = (room: MeetingRoom) =>
    room.participants.every((p: Participant) => p.name !== "");

  // 방 만들기
  const handleCreateRoom = () => {
    setModalVisible(true);
  };

  const handleAddRoom = () => {
    if (!roomTitle.trim()) return;
    const m :number = selectedPreset.male;
    const f :number = selectedPreset.female;
    const newId :number = (meetingRooms.length ? Math.max(...meetingRooms.map(r => r.id)) : 0) + 1;
    const participants: Participant[] = [
      ...Array(m).fill(null).map((_, idx) => ({ id: newId * 100 + idx + 1, name: "", gender: "남" as Gender })),
      ...Array(f).fill(null).map((_, idx) => ({ id: newId * 100 + m + idx + 1, name: "", gender: "여" as Gender })),
    ];
    const type: 'pair' | 'mixed' = selectedPreset.type;
    const title :string = roomTitle;
    setMeetingRooms([...meetingRooms, { id: newId, title, participants, type }]);
    setModalVisible(false);
    setRoomTitle('');
    setSelectedPreset(ROOM_PRESETS[0]);
  };

  return (
    <LinearGradient
      colors={['#FFB1E8', '#EC75FF', '#947CFF', '#F0F0E9', '#F0F0E9']}
      locations={[0, 0.28, 0.54, 0.9, 1]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0, y: 0.5 }}
      style={styles.container}
    >
      <Header title="UniMeet" onNotificationPress={() => alert('홈 화면에서 알림을 눌렀습니다!')} />

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
          <TouchableOpacity onPress={() => alert('설명하는 기능 준비중')}>
            <Ionicons name="help-circle-outline" size={20} style={styles.icon} />
          </TouchableOpacity>
          <View style={{ flex: 1 }} />
          <TouchableOpacity style={styles.createRoomBtn} onPress={handleCreateRoom}>
            <Ionicons name="add-circle-outline" size={15} style={{ marginRight: 2 }} />
            <Text style={styles.createRoomBtnText}>방만들기</Text>
          </TouchableOpacity>
        </View>

        {meetingRooms.map((room) => {
          const maleTotal :number = room.participants.filter(p => p.gender === "남").length;
          const femaleTotal :number = room.participants.filter(p => p.gender === "여").length;
          const isPureGender :boolean = room.type !== 'mixed';
          const isMixed :boolean = room.type === 'mixed';
          return (
            <View key={room.id} style={styles.meetingCard}>
              <View style={styles.cardHeader}>
                <Text style={styles.cardTitle}>{room.title}</Text>
                <View style={styles.statusRow}>
                  <Text style={styles.peopleCount}>{getPeopleLabel(room)}</Text>
                  <Text
                    style={[
                      styles.roomStatus,
                      isRoomFull(room) ? styles.fullStatus : styles.openStatus,
                    ]}
                  >
                    {isRoomFull(room) ? "인원이 다 찼어요" : "참여가 가능해요"}
                  </Text>
                </View>
              </View>
              <View style={styles.participantsContainer}>
                {isMixed ? (
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
          );
        })}
      </ScrollView>

      {/* 방 만들기 모달 */}
      <Modal
        visible={modalVisible}
        transparent
        animationType="fade"
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContainer}>
            {/* 헤더 */}
            <LinearGradient
              colors={['#947CFF', '#EC75FF']}
              start={{x: 0, y: 0}}
              end={{x: 1, y: 0}}
              style={styles.modalHeader}
            >
              <Text style={styles.modalHeaderText}>새로운 미팅방</Text>
            </LinearGradient>

            {/* 본문 */}
            <View style={styles.modalBody}>
              <Text style={styles.inputLabel}>방 제목</Text>
              <TextInput
                style={styles.input}
                placeholder="예) 즐겁게 놀아요"
                placeholderTextColor="#A0A0A0"
                value={roomTitle}
                onChangeText={setRoomTitle}
              />

              <Text style={styles.presetLabel}>유형 선택</Text>
              <View style={styles.presetContainer}>
                {ROOM_PRESETS.map((preset) => (
                  <TouchableOpacity
                    key={preset.label}
                    style={[
                      styles.presetButton,
                      selectedPreset.label === preset.label && styles.presetButtonSelected
                    ]}
                    onPress={() => setSelectedPreset(preset)}
                  >
                    <Ionicons 
                      name={preset.type === 'mixed' ? 'people' : 'male-female'} 
                      size={20} 
                      color={selectedPreset.label === preset.label ? '#FFF' : '#947CFF'} 
                    />
                    <Text style={[
                      styles.presetText,
                      selectedPreset.label === preset.label && styles.presetTextSelected
                    ]}>
                      {preset.label}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </View>

            {/* 버튼 그룹 */}
            <View style={styles.buttonGroup}>
              <TouchableOpacity 
                style={styles.cancelButton}
                onPress={() => setModalVisible(false)}
              >
                <Text style={styles.cancelButtonText}>닫기</Text>
              </TouchableOpacity>
              <LinearGradient
                colors={['#947CFF', '#EC75FF']}
                style={styles.createButton}
                start={{x: 0, y: 0}}
                end={{x: 1, y: 0}}
              >
                <TouchableOpacity onPress={handleAddRoom}>
                  <Text style={styles.createButtonText}>
                    <Ionicons name="rocket" size={14} /> 생성하기
                  </Text>
                </TouchableOpacity>
              </LinearGradient>
            </View>
          </View>
        </View>
      </Modal>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
  content: {
    paddingTop: 10,
    paddingHorizontal: 15,
    alignItems: 'center'
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
    fontSize:11,
    color: '#3D3D3D'
  },
  guideBox: {
    width: '100%',
    height: 100,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor:
    '#D1D0D0',
    marginBottom: 30,
  },
  guideBoxText: {
    textAlign: 'center',
    color: '#3D3D3D'
  },
  text1: { fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 2,
    marginLeft: 5
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
    color: '#3D3D3D'
  },
  createRoomBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 5,
    paddingHorizontal: 8
  },
  createRoomBtnText: {
    fontWeight: '600',
    fontSize: 12
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
    alignItems: 'center',
    marginBottom: 10,
    marginLeft: 13,
  },
  cardTitle: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#333',
    marginRight: 7,
  },
  statusRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  peopleCount: {
    fontSize: 10,
    backgroundColor: '#2C2C2C',
    color: '#FFFFFF',
    paddingTop: 2,
    paddingBottom: 2,
    paddingLeft: 10,
    paddingRight: 10,
    borderRadius: 15,
    marginRight: 5,
  },
  roomStatus: {
    fontSize: 11,
  },
  fullStatus: {
    color: '#FF6666',
  },
  openStatus: {
    color: '#767676',
  },
  participantsContainer: {
    flexDirection: 'column',
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    paddingHorizontal: 10,
    width: '100%',
  },
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
    shadowColor: '#000',
    shadowOffset: { width: 1, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 2,
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
    elevation: 2,
  },
  genderLabelMale: {
    fontWeight: '500',
    color: '#767676',
    fontSize: 12,
    marginRight: 8,
    minWidth: 20,
    textAlignVertical: 'center',
  },
  genderLabelFemale: {
    fontWeight: '500',
    color: '#767676',
    fontSize: 12,
    marginRight: 8,
    minWidth: 20,
    textAlignVertical: 'center',
  },
  participantsWrap: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center'
  },
  participantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
    minWidth: 70 },
  mixedBox: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F0F0F0',
    borderRadius: 10,
    padding: 10,
    minHeight: 60, width: '100%',
    shadowColor: '#000',
    shadowOffset: { width: 1, height: 1 },
    shadowOpacity: 0.2,
    shadowRadius: 3,
    elevation: 2,
  },
  mixedLabel: {
    fontWeight: '500',
    color: '#767676',
    fontSize: 12,
    minWidth: 28,
    textAlignVertical: 'center',
  },
  mixedParticipantsWrap: {
    flex: 1,
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center'
  },
  mixedParticipantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 7,
    minWidth: 120,
    marginLeft: 25
  },
  male: {
    color: '#6846FF',
    marginRight: 5
  },
  female: {
    color: '#FF62D5',
    marginRight: 5
  },
  mixedIcon: {
    color: '#FF9800',
    marginRight: 5
  },
  participantText: {
    fontSize: 12
  },
  noName: {
    color: '#767676',
    fontStyle: 'italic'
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalContainer: {
    width: '85%',
    borderRadius: 20,
    overflow: 'hidden',
    backgroundColor: '#FFF',
  },
  modalHeader: {
    padding: 20,
    alignItems: 'center',
  },
  modalHeaderText: {
    color: 'white',
    fontSize: 20,
    fontWeight: '800',
    letterSpacing: 0.8,
  },
  modalBody: {
    padding: 25,
  },
  inputLabel: {
    color: '#444',
    fontSize: 14,
    marginBottom: 8,
    fontWeight: '600',
  },
  input: {
    borderWidth: 1.5,
    borderColor: '#EEE',
    borderRadius: 12,
    padding: 14,
    fontSize: 15,
    marginBottom: 25,
    backgroundColor: '#FAFAFA',
  },
  presetLabel: {
    color: '#444',
    fontSize: 14,
    marginBottom: 12,
    fontWeight: '600',
  },
  presetContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
  },
  presetButton: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 18,
    borderRadius: 10,
    borderWidth: 1.5,
    borderColor: '#EEE',
    gap: 8,
  },
  presetButtonSelected: {
    backgroundColor: '#947CFF',
    borderColor: '#947CFF',
  },
  presetText: {
    fontSize: 13,
    color: '#666',
    fontWeight: '500',
  },
  presetTextSelected: {
    color: '#FFF',
  },
  buttonGroup: {
    flexDirection: 'row',
    borderTopWidth: 1,
    borderTopColor: '#F0F0F0',
  },
  cancelButton: {
    flex: 0.8,
    padding: 18,
    alignItems: 'center',
    backgroundColor: '#FFF',
  },
  cancelButtonText: {
    color: '#666',
    fontWeight: '500',
  },
  createButton: {
    flex: 1,
  },
  createButtonText: {
    color: '#FFF',
    fontWeight: '700',
    textAlign: 'center',
    padding: 16,
  },  
});

export default Home;
