import React from 'react';
import { View, Text, StyleSheet, ScrollView, Image, TouchableOpacity } from 'react-native';
import Header from '../../navigation/Header';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';
import type { RootStackParamList } from '../../navigation/types';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

const Profile: React.FC = () => {
  const handleNotificationPress = () => {
    alert('프로필 화면에서 알림을 눌렀습니다!');
  };

  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  const interests: string[] = ['#운동', '#축구', '#게임', '#음악', '#영화', '#드라마'];

  const myInfoList: {
    label: string;
    value: string;
        }[] = [
        { label: '나의 친구 목록', value: '0명'},
        { label: '학과', value: '컴퓨터공학과' },
        { label: '학번', value: '20학번' },
        { label: '나이', value: '25' },
        { label: '키', value: '175cm' },
        { label: '전화번호', value: '010-3200-1951' },
  ];
  

  return (
    <LinearGradient
    colors={['#FF87DD', '#B092FF', '#DBD6EC', '#F0F0E9']}
      locations={[0, 0.43, 0.71, 0.93]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0, y: 0.35 }}
      style={styles.container}
    >
      <View style={styles.container}>
        {/* 헤더 */}
        <Header
            title="마이페이지"
            onNotificationPress={handleNotificationPress}
            iconName="settings-outline"
        />


        <ScrollView contentContainerStyle={styles.content}>

          <View style={styles.introduce}>
            <Image source={require('../../img/Profile.jpg')} style={styles.image} />
            <View style={styles.infoBox}>
              <Text style={styles.label}>
                이름: <Text style={styles.value}>이동연</Text>
              </Text>
              <Text style={styles.label}>
                닉네임: <Text style={styles.value}>강아지똥</Text>
              </Text>
              <Text style={styles.label}>
                생년월일: <Text style={styles.value}>2001.01.23</Text>
              </Text>
              <TouchableOpacity onPress={() => navigation.navigate('ProfileDetail')}>
                <Text>나의 프로필 보기</Text>
              </TouchableOpacity>
            </View>
          </View>

          <View style={styles.myInfo}>
            {myInfoList.map((item, idx) => (
                <React.Fragment key={item.label}>
                <View style={styles.infoRow}>
                    <Text style={styles.infoLabel}>{item.label}</Text>
                    <Text style={styles.infoValue}>{item.value}</Text>
                </View>
                {/* 마지막 줄에는 밑줄을 그리지 않으려면 아래 조건 추가 */}
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

          <View>
            <Text>로그아웃 | 회원탈퇴</Text>
          </View>

        </ScrollView>
      </View>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  icon: {
    marginRight: 5,
  },
  introduce: {
    width: '90%',
    flexDirection: 'row',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#f9f9f9',
    borderRadius: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 5,
    marginBottom: 10,
  },
  image: {
    width: 100,
    height: 100,
    borderRadius: 50,
    marginRight: 40,
  },
  infoBox: {
    flex: 1,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 6,
    color: '#333',
  },
  value: {
    fontWeight: '400',
    color: '#555',
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

export default Profile;
