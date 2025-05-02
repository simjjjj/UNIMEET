import React from 'react';
import { View, Text, StyleSheet, ScrollView, Image } from 'react-native';
import Header from '../navigation/Header';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';

const Profile: React.FC = () => {
  const handleNotificationPress = () => {
    alert('프로필 화면에서 알림을 눌렀습니다!');
  };

  const interests: string[] = ['#운동', '#축구', '#게임', '#음악', '#영화', '#드라마'];

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
        <Header title="마이페이지" onNotificationPress={handleNotificationPress} />

        <ScrollView contentContainerStyle={styles.content}>
          <View style={styles.comment}>
            <Ionicons name="rocket-outline" size={12} color="#3D3D3D" style={styles.icon} />
            <Text style={styles.commentText}>프로필을 수정할 수 있는 공간입니다.</Text>
          </View>
          <View style={styles.introduce}>
            <Image source={require('../img/Profile.jpg')} style={styles.image} />
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
            </View>
          </View>
          <View style={styles.myInfo}>
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>학과</Text>
              <Text style={styles.infoValue}>컴퓨터공학과</Text>
            </View>
            <View style={styles.underline} />
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>학번</Text>
              <Text style={styles.infoValue}>20학번</Text>
            </View>
            <View style={styles.underline} />
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>나이</Text>
              <Text style={styles.infoValue}>25</Text>
            </View>
            <View style={styles.underline} />
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>키</Text>
              <Text style={styles.infoValue}>175cm</Text>
            </View>
            <View style={styles.underline} />
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>전화번호</Text>
              <Text style={styles.infoValue}>010-3200-1951</Text>
            </View>
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
  comment: {
    width: '90%',
    height: 30,
    backgroundColor: '#D1D0D0',
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'row',
    marginBottom: 10,
  },
  commentText: {
    fontSize: 11,
    color: '#3D3D3D',
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
    marginTop: 10,
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
