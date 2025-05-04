import React from 'react';
import { View, Text, StyleSheet, ScrollView, Image, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';
import { LinearGradient } from 'expo-linear-gradient';

const ProfileDetail: React.FC = () => {
  const navigation = useNavigation();

  const interests: string[] = ['#운동', '#축구', '#게임', '#음악', '#영화', '#드라마'];

  const myInfoList = [
    { label: '이름', value: '이동연' },
    { label: '닉네임', value: '강아지똥' },
    { label: '생년월일', value: '2001.01.23' },
    { label: '학과', value: '컴퓨터공학과' },
    { label: '학번', value: '20학번' },
    { label: '나이', value: '25' },
    { label: '키', value: '175cm' },
    { label: '전화번호', value: '010-3200-1951' },
    { label: '가입일', value: '2025.01.02' },
  ];

  return (
    <LinearGradient
      colors={['#FF87DD', '#B092FF', '#DBD6EC', '#F0F0E9']}
      locations={[0, 0.43, 0.71, 0.93]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0, y: 0.35 }}
      style={styles.container}
    >
      {/* 헤더 */}
      <View style={styles.headerRow}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.sideButton}>
          <Ionicons name="arrow-back" size={28} color="#fff" />
        </TouchableOpacity>
        <View style={styles.titleBox}>
          <Text style={styles.title}>나의 프로필 상세 정보</Text>
        </View>
        <TouchableOpacity onPress={() => alert('수정화면으로 이동')} style={styles.sideButton}>
          <Ionicons name="create-outline" size={24} color="#fff" />
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <Image source={require('../../img/Profile.jpg')} style={styles.image} />

        <View style={styles.myInfo}>
          {myInfoList.map((item, idx) => (
            <React.Fragment key={item.label}>
              <View style={styles.infoRow}>
                <Text style={styles.infoLabel}>{item.label}</Text>
                <Text style={styles.infoValue}>{item.value}</Text>
              </View>
              {idx < myInfoList.length - 1 && <View style={styles.underline} />}
            </React.Fragment>
          ))}
        </View>

        <View style={styles.interests}>
          <View style={[styles.infoRow, { alignItems: 'flex-start' }]}>
            <Text style={styles.infoIsLabel}>MBTI</Text>
            <Text style={styles.infoMBTI}>ESTP</Text>
          </View>

          <View style={styles.underline} />
            <View style={[styles.infoRow, { alignItems: 'flex-start' }]}>
                <Text style={styles.infoIsLabel}>관심사</Text>
                <View style={styles.interestTags}>
                  {interests.map((item, i) => (
                    <Text key={i} style={styles.infoInterest}>
                      {item}
                  </Text>
                ))}
            </View>
          </View>
        </View>
      </ScrollView>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 50,
  },
  headerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    marginBottom: 24,
    height: 48,
    position: 'relative',
  },
  sideButton: {
    width: 40,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 2,
  },
  titleBox: {
    position: 'absolute',
    left: 0,
    right: 0,
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1,
    pointerEvents: 'none', // 버튼 클릭 방해 방지
  },
  title: {
    fontSize: 18,
    fontWeight: '500',
    color: 'white',
    textAlign: 'center',
    textShadowColor: 'rgba(0,0,0,0.75)',
    textShadowOffset: { width: 3, height: 2 },
    textShadowRadius: 5,
  },
  content: {
    flexGrow: 1,
    alignItems: 'center',
  },
  image: {
    width: 250,
    height: 250,
    borderRadius: 50,
    marginBottom: 20,
    alignSelf: 'center',
  },
  myInfo: {
    width: '90%',
    backgroundColor: '#f9f9f9',
    borderRadius: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
    padding: 20,
    marginBottom: 10,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  infoLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginLeft: 10,
    width: 180,
  },
  infoValue: {
    fontSize: 16,
    color: '#555',
    textAlign: 'left',
    flex: 1,
  },
  underline: {
    width: '100%',
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    marginVertical: 1,
    marginTop: 15,
    marginBottom: 15,
  },
  interests: {
    width: '90%',
    backgroundColor: '#f9f9f9',
    borderRadius: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
    padding: 20,
    marginBottom: 20,
  },
  infoIsLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    padding: 3,
  },
  infoMBTI: {
    fontSize: 14,
    fontWeight: '600',
    color: '#555',
    backgroundColor: 'skyblue',
    paddingTop: 5,
    paddingBottom: 5,
    paddingLeft: 10,
    paddingRight: 10,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
    marginRight: 100,
  },
  interestTags: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    maxWidth: '75%',
    marginLeft: 8,
  },
  infoInterest: {
    fontSize: 12,
    color: '#FFFFFF',
    backgroundColor: 'grey',
    paddingTop: 7,
    paddingBottom: 7,
    paddingLeft: 12,
    paddingRight: 12,
    borderRadius: 15,
    marginRight: 10,
    marginBottom: 10,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
  },
});

export default ProfileDetail;
