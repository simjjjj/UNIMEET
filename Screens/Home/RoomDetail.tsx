import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { RouteProp, useRoute, useNavigation } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import type { RootStackParamList } from '../../navigation/types';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

const RoomDetail: React.FC = () => {
    const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
    const route = useRoute<RouteProp<RootStackParamList, 'RoomDetail'>>();
    const { room } = route.params;

  const maleList = room.participants.filter(p => p.gender === '남');
  const femaleList = room.participants.filter(p => p.gender === '여');
  const isMixed = room.type === 'mixed';

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
        <View style={{ width: 34 }} />
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.comment}>
            <Ionicons name="rocket-outline" size={12} color="#3D3D3D" style={styles.icon} />
            <Text style={styles.commentText}>여기는 개발자 코멘트를 적는 곳.</Text>
        </View>

        <View>
            <Text>{ room.title }</Text>
        </View>

        <View>

        </View>


        <View style={styles.infoBox}>
          <Text style={styles.roomLabel}>방 유형</Text>
          <Text style={styles.roomValue}>{isMixed ? '혼성' : '남/여'}</Text>
        </View>
        <View style={styles.infoBox}>
          <Text style={styles.roomLabel}>총 인원</Text>
          <Text style={styles.roomValue}>{room.participants.length}인</Text>
        </View>

        <View style={styles.sectionTitleRow}>
          <Ionicons name="people" size={18} color="#FFFFFF" />
          <Text style={styles.sectionTitle}>참가자 목록</Text>
        </View>

        {isMixed ? (
          <View style={styles.participantGroupBox}>
            {room.participants.map(p => (
              <View key={p.id} style={styles.participantRow}>
                <Ionicons name="person" size={16} color="#FF9800" style={{ marginRight: 7 }} />
                <Text style={[styles.participantText, !p.name && styles.noName]}>
                  {p.name ? p.name : '없음'} <Text style={{ color: '#aaa' }}>({p.gender})</Text>
                </Text>
              </View>
            ))}
          </View>
        ) : (
          <View style={{ flexDirection: 'row', gap: 10 }}>
            <View style={styles.participantGroupBox}>
              <Text style={styles.groupLabel}>남</Text>
              {maleList.map(p => (
                <View key={p.id} style={styles.participantRow}>
                  <Ionicons name="person" size={15} color="#6846FF" style={{ marginRight: 7 }} />
                  <Text style={[styles.participantText, !p.name && styles.noName]}>
                    {p.name ? p.name : '없음'}
                  </Text>
                </View>
              ))}
            </View>
            <View style={styles.participantGroupBox}>
              <Text style={styles.groupLabel}>여</Text>
              {femaleList.map(p => (
                <View key={p.id} style={styles.participantRow}>
                  <Ionicons name="person" size={15} color="#FF62D5" style={{ marginRight: 7 }} />
                  <Text style={[styles.participantText, !p.name && styles.noName]}>
                    {p.name ? p.name : '없음'}
                  </Text>
                </View>
              ))}
            </View>
          </View>
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
    paddingTop: 60,
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
    textShadowOffset: { width: 1, height: 2 },
    textShadowRadius: 4,
    elevation: 4,
  },
  content: {
    paddingTop: 10,
    paddingHorizontal: 15,
    alignItems: 'center'
  },
  infoBox: {
    flexDirection: 'row',
    width: '85%',
    justifyContent: 'space-between',
    marginTop: 10,
    marginBottom: 2,
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 14,
    elevation: 1,
  },
  roomLabel: {
    color: '#947CFF',
    fontWeight: 'bold',
    fontSize: 14,
  },
  roomValue: {
    color: '#444',
    fontSize: 14,
    fontWeight: '500',
  },
  sectionTitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 28,
    marginBottom: 8,
    gap: 7,
  },
  sectionTitle: {
    fontSize: 17,
    fontWeight: '600',
    color: '#6846FF',
    marginLeft: 5,
  },
  participantGroupBox: {
    backgroundColor: '#F7F6FF',
    borderRadius: 12,
    padding: 15,
    minWidth: 120,
    margin: 6,
    marginBottom: 16,
    alignItems: 'flex-start',
    flex: 1,
  },
  groupLabel: {
    fontSize: 15,
    color: '#888',
    fontWeight: 'bold',
    marginBottom: 7,
  },
  participantRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 9,
  },
  participantText: {
    fontSize: 14,
    color: '#333',
  },
  noName: {
    color: '#BDBDBD',
    fontStyle: 'italic',
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
});

export default RoomDetail;
