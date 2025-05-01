import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import Header from '../navigation/Header';

const Profile :React.FC = () => {
  const handleNotificationPress = () => {
    alert('프로필 화면에서 알림을 눌렀습니다!');
  };

  return (
    <View style={styles.container}>
      {/* 헤더 */}
      <Header title="프로필" onNotificationPress={handleNotificationPress} />

      <ScrollView contentContainerStyle={styles.content}>
        {/* 메인 콘텐츠 */}
        <Text style={styles.text}>프로필 화면</Text>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 20,
  },
});

export default Profile;
