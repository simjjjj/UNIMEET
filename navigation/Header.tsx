import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface HeaderProps {
  title: string;
  onNotificationPress: () => void;
}

interface HeaderProps {
    title: string;
    onNotificationPress: () => void;
    iconName?: string; // 아이콘 이름을 선택적으로 받음
  }
  
  const Header: React.FC<HeaderProps> = ({ title, onNotificationPress, iconName = "notifications-outline" }) => {
    return (
      <View style={styles.header}>
        <Text style={styles.title}>{title}</Text>
        <TouchableOpacity style={styles.notificationButton} onPress={onNotificationPress}>
        <Ionicons name={iconName as any} size={22} color="#000" />
        </TouchableOpacity>
      </View>
    );
  };
  

const styles = StyleSheet.create({
  header: {
    paddingTop: 60,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 15,
    paddingBottom: 10,
    backgroundColor: 'transparent', // 배경 투명
  },
  title: {
    fontSize: 18,
    fontWeight: '500',
    flex: 1,
    left: 10,
    color: 'white',
    textShadowColor: 'rgba(0,0,0,0.75)',
    textShadowOffset: { width: 3, height: 2 },
    textShadowRadius: 5,
  },
  notificationButton: {
    padding: 10,
  },
  leftSpace: {
    width: 24,
  },
});

export default Header;
