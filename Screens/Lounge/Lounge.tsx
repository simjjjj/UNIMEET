import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import Header from '../../navigation/Header';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';

const dummyPosts = [
  {
    id: 1,
    title: '오늘 점메추좀',
    text: '점심 메뉴를 도저히 못 고르겠어요',
    author: '익명',
    date: '1분전',
    comments: 4,
    likes: 3,
  },
  {
    id: 2,
    title: '스터디 모집합니다! (React Native)',
    text: '어플개발 고수 가보자',
    author: '익명',
    date: '27분전',
    comments: 2,
    likes: 1,
  },
  {
    id: 3,
    title: '어제 보름달',
    text: '어제 완전 보름달이고 날도 맑고 달도 밝았다던데 사진 찍으신 분 있으신가요 ㅜㅜ',
    author: '익명',
    date: '42분전',
    comments: 8,
    likes: 7,
  },
  {
    id: 4,
    title: '자유게시판 이용규칙 안내',
    author: '운영자',
    date: '2025-05-22',
    comments: 0,
    notice: true,
    likes: 0,
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
        <Header
          title="라운지"
          onNotificationPress={handleNotificationPress}
          iconName="search"
        />

        <ScrollView contentContainerStyle={styles.content}>
          <View style={styles.comment}>
            <Ionicons name="megaphone-outline" size={12} color="#3D3D3D" style={styles.icon} />
            <Text style={styles.commentText}>비적절한 게시글, 댓글은 신고 대상입니다.</Text>
          </View>

          {/* 게시글 리스트 */}
          <View style={styles.postList}>
            {dummyPosts
              .filter(post => !post.notice)
              .map(post => (
                <View key={post.id} style={styles.postCard}>
                  <Text style={styles.postTitle}>{post.title}</Text>
                  <Text style={styles.postText}>{post.text}</Text>
                  <View style={styles.postMeta}>
                    <Text style={styles.postAuthor}>{post.author}</Text>
                    <Text style={styles.postDate}>{post.date}</Text>
                    <Ionicons name="chatbubble-ellipses-outline" size={14} color="#B092FF" style={{ marginLeft: 8, marginRight: 2 }} />
                    <Text style={styles.postComments}>{post.comments}</Text>
                    <Ionicons name="heart-outline" size={15} color="#FF6B81" style={{ marginLeft: 6, marginRight: 2 }} />
                    <Text style={styles.postLikes}>{post.likes}</Text>
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
    padding: 10,
    paddingHorizontal: 15,
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
    padding: 15,
    marginBottom: 14,
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 3 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 5,
  },
  postTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#3D3D3D',
    marginBottom: 7,
  },
  postText: {
    color: '#606060',
    fontSize: 13,
    marginBottom: 7,
  },
  postMeta: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  postAuthor: {
    fontSize: 11,
    color: '#6846FF',
    marginRight: 5,
  },
  postDate: {
    fontSize: 11,
    color: '#AAA',
  },
  postComments: {
    fontSize: 11,
    color: '#B092FF',
    fontWeight: 'bold',
  },
  postLikes: {
    fontSize: 11,
    color: '#FF6B81',
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
