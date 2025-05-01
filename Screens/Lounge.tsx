import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import Header from '../navigation/Header';

const Lounge: React.FC = () => {
  const handleNotificationPress = (): void => {
    alert('라운지 화면에서 알림을 눌렀습니다!');
  };

  return (
    <View style={styles.container}>
      {/* 헤더 */}
      <Header title="라운지" onNotificationPress={handleNotificationPress} />

      <ScrollView contentContainerStyle={styles.content}>
        {/* 메인 콘텐츠 */}
        <Text style={styles.text}>라운지 화면</Text>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flexGrow: 1, // ScrollView에서 flex: 1 대신 flexGrow 사용
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 20,
  },
});

export default Lounge;
