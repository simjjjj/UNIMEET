import React, { useState } from 'react';
import { View, Text, StyleSheet, Switch, TouchableOpacity, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import { useNavigation } from '@react-navigation/native';

const settingsList = [
  {
    label: '알림 설정',
    icon: 'notifications-outline',
    type: 'switch',
    key: 'notification',
  },
  {
    label: '계정 관리',
    icon: 'person-outline',
    onPress: () => Alert.alert('계정 관리로 이동'),
  },
  {
    label: '앱 정보',
    icon: 'information-circle-outline',
    onPress: () => Alert.alert('앱 정보로 이동'),
  },
  {
    label: '문의하기',
    icon: 'chatbubble-outline',
    onPress: () => Alert.alert('문의하기로 이동'),
  },
  {
    label: '신고하기',
    icon: 'flag-outline',
    onPress: () => Alert.alert('신고하기로 이동'),
  },
  {
    label: '로그아웃',
    icon: 'log-out-outline',
    onPress: () => Alert.alert('로그아웃 되었습니다.'),
  },
  {
    label: '회원탈퇴',
    icon: 'close-circle-outline',
    onPress: () => Alert.alert('회원탈퇴 안내 페이지로 이동'),
  },
];

const Settings: React.FC = () => {
  const navigation = useNavigation();
  const [isNotificationOn, setIsNotificationOn] = useState(true);

  return (
    <LinearGradient
      colors={['#FF87DD', '#B092FF', '#DBD6EC', '#F0F0E9']}
      locations={[0, 0.43, 0.71, 0.93]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0, y: 0.35 }}
      style={styles.container}
    >
      <View style={styles.headerRow}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backButton}>
          <Ionicons name="arrow-back" size={28} color="#fff" />
        </TouchableOpacity>
        <View style={styles.titleContainer}>
          <Text style={styles.title}>환경설정</Text>
        </View>
        <View style={{ width: 46 }} />
      </View>

      <View style={styles.settingBox}>
        {settingsList.map((item, index) => (
          <View key={item.label}>
            {item.type === 'switch' ? (
              <View style={[
                styles.settingRow,
                index === settingsList.length - 1 && styles.lastRow
              ]}>
                <View style={styles.settingLeft}>
                  <Ionicons name={item.icon as any} size={22} color="#6846FF" style={{ marginRight: 12 }} />
                  <Text style={styles.settingLabel}>{item.label}</Text>
                </View>
                <Switch
                  value={isNotificationOn}
                  onValueChange={setIsNotificationOn}
                  thumbColor={isNotificationOn ? '#6846FF' : '#ccc'}
                  trackColor={{ true: '#CF59DF', false: '#ccc' }}
                />
              </View>
            ) : (
              <TouchableOpacity 
                style={[
                  styles.settingRow, 
                  index === settingsList.length - 1 && styles.lastRow
                ]} 
                onPress={item.onPress}
              >
                <View style={styles.settingLeft}>
                  <Ionicons name={item.icon as any} size={22} color="#6846FF" style={{ marginRight: 12 }} />
                  <Text style={styles.settingLabel}>{item.label}</Text>
                </View>
                <Ionicons name="chevron-forward" size={20} color="#bbb" />
              </TouchableOpacity>
            )}
          </View>
        ))}
      </View>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, paddingTop: 50 },
  headerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    marginBottom: 24,
    height: 48,
    position: 'relative',
    marginTop: 10,
  },
  backButton: {
    padding: 8,
    zIndex: 1,
  },
  titleContainer: {
    position: 'absolute',
    left: 0,
    right: 0,
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: 'white',
    textAlign: 'center',
    textShadowColor: 'rgba(0,0,0,0.75)',
    textShadowOffset: { width: 2, height: 1 },
    textShadowRadius: 5,
  },
  settingBox: {
    backgroundColor: '#fff',
    borderRadius: 18,
    marginHorizontal: 18,
    paddingVertical: 10,
    paddingHorizontal: 4,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowRadius: 8,
  },
  settingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 18,
    paddingHorizontal: 18,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  lastRow: {
    borderBottomWidth: 0, // 마지막 줄 밑줄 제거
  },
  settingLeft: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  settingLabel: {
    fontSize: 16,
    color: '#333',
    fontWeight: '500',
  },
});

export default Settings;
