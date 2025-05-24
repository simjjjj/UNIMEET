import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import Header from '../../navigation/Header';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';

const dummyPosts = [
  {
    id: 1,
    title: '오늘 점심 뭐 먹었어요?',
    author: '홍길동',
    date: '2025-05-24',
    comments: 4,
  },
  {
    id: 2,
    title: '스터디 모집합니다! (React Native)',
    author: '김영희',
    date: '2025-05-23',
    comments: 2,
  },
  {
    id: 3,
    title: '자유게시판 이용규칙 안내',
    author: '운영자',
    date: '2025-05-22',
    comments: 0,
    notice: true,
  },
];

const Lounge: React.FC = () => {
  const handleNotificationPress = (): void => {
    alert('라운지 화면에서 알림을 눌렀습니다!');
  };

  const handleWritePress = () => {
    alert('글쓰기 버튼 클릭!');
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
        <Header title="라운지" onNotificationPress={handleNotificationPress} />

        <ScrollView contentContainerStyle={styles.content}>
          <View style={styles.comment}>
            <Ionicons name="megaphone-outline" size={12} color="#3D3D3D" style={styles.icon} />
            <Text style={styles.commentText}>커뮤니티 페이지입니다.</Text>
          </View>

          {/* 게시글 리스트 */}
          <View style={styles.postList}>
            {dummyPosts
              .filter(post => !post.notice)
              .map(post => (
                <View key={post.id} style={styles.postCard}>
                  <Text style={styles.postTitle}>{post.title}</Text>
                  <View style={styles.postMeta}>
                    <Text style={styles.postAuthor}>{post.author}</Text>
                    <Text style={styles.postDate}>{post.date}</Text>
                    <Ionicons name="chatbubble-ellipses-outline" size={14} color="#B092FF" style={{ marginLeft: 8, marginRight: 2 }} />
                    <Text style={styles.postComments}>{post.comments}</Text>
                  </View>
                </View>
              ))}
          </View>
        </ScrollView>

        <TouchableOpacity style={styles.fab} onPress={handleWritePress}>
          <Ionicons name="pencil" size={22} color="#fff" />
        </TouchableOpacity>
      </View>
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  comment: {
    width: '100%',
    height: 28,
    backgroundColor: '#FFFFFF',
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
    flexDirection: 'row',
  },
  commentText: {
    fontSize:11,
    color: '#3D3D3D'
  },
  icon: {
    marginRight: 5,
    marginBottom: 2,
    color: '#3D3D3D'
  },
  content: {
    flexGrow: 1,
    padding: 20,
    paddingBottom: 80,
  },
  noticeCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F6F2FF',
    borderRadius: 8,
    padding: 10,
    marginBottom: 14,
    shadowColor: '#B092FF',
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  noticeText: {
    fontSize: 14,
    color: '#6846FF',
    fontWeight: 'bold',
  },
  postList: {
    marginTop: 10,
  },
  postCard: {
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 14,
    marginBottom: 14,
    shadowColor: '#B092FF',
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
  },
  postTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#3D3D3D',
    marginBottom: 7,
  },
  postMeta: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  postAuthor: {
    fontSize: 12,
    color: '#6846FF',
    marginRight: 10,
  },
  postDate: {
    fontSize: 11,
    color: '#AAA',
    marginRight: 10,
  },
  postComments: {
    fontSize: 12,
    color: '#B092FF',
    fontWeight: 'bold',
  },
  fab: {
    position: 'absolute',
    right: 22,
    bottom: 32,
    backgroundColor: '#6846FF',
    width: 54,
    height: 54,
    borderRadius: 27,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#6846FF',
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 6,
  },
});

export default Lounge;
