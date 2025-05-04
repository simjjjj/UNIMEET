import React from 'react';
import { View, Text, StyleSheet, ScrollView, Image, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';
import { LinearGradient } from 'expo-linear-gradient';

const MyProfileDetail: React.FC = () => {
  const navigation = useNavigation();

  const myInfoList: {
    label: string;
    value: string;
        }[] = [
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

        <View style={styles.headerRow}>
          <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backButton}>
            <Ionicons name="arrow-back" size={28} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.title}>나의 프로필 상세 정보</Text>
          {/* 오른쪽에 투명한 View로 공간을 맞춰줌 */}
          <View style={{ width: 36 }} />
        </View>
        
        <ScrollView contentContainerStyle={styles.content}>
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

        </ScrollView>
      </View>
      
      
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 30,
  },
  content: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  headerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 16,
    marginBottom: 24,
  },
  backButton: {
  },
  title: {
    flex: 1,
    fontSize: 18,
    fontWeight: '500',
    color: 'white',
    textAlign: 'center',
    textShadowColor: 'rgba(0,0,0,0.75)',
    textShadowOffset: { width: 3, height: 2 },
    textShadowRadius: 5,
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
});

export default MyProfileDetail;
