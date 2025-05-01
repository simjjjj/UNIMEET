import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';


interface HeaderProps {
    title: string;
    onNotificationPress: () => void;
  }

  const Header: React.FC<HeaderProps> = ({ title, onNotificationPress }) => {
    return (
      <View style={styles.header}>
        <Text style={styles.title}>{title}</Text>
        <TouchableOpacity style={styles.notificationButton} onPress={onNotificationPress}>
          <Ionicons name="notifications-outline" size={22} color="#000" />
        </TouchableOpacity>
      </View>
    );
  };

const styles = StyleSheet.create({
  header: {
    paddingTop: 60,
    backgroundColor: '#f8f9fa',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    paddingBottom: 10,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    flex: 1,
    left: 10,
  },
  notificationButton: {
    padding: 10,
  },
  leftSpace: {
    width: 24, // 오른쪽 아이콘과 균형을 맞추기 위한 공간
  },
});

export default Header;
