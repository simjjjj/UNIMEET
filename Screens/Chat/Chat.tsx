import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import Header from '../../navigation/Header';
import { LinearGradient } from 'expo-linear-gradient';

const Chat :React.FC = () => {
  const handleNotificationPress = () => {
    alert('채팅 화면에서 알림을 눌렀습니다!');
  };

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
            <Header title="채팅" onNotificationPress={handleNotificationPress} />


            {/* 메인 콘텐츠부분 */}
            <ScrollView contentContainerStyle={styles.content}>
                <Text style={styles.text}>채팅 화면</Text>
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
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    fontSize: 20,
  },
});

export default Chat;
