import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const MyProfileDetail: React.FC = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>나의 프로필 상세 정보</Text>
      {/* 여기에 원하는 상세 정보 컴포넌트/레이아웃 추가 */}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  title: { fontSize: 20, fontWeight: 'bold' },
});

export default MyProfileDetail;
