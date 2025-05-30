import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Platform } from 'react-native';
import { RouteProp, useRoute, useNavigation } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import type { RootStackParamList } from '../../navigation/types';
import { LinearGradient } from 'expo-linear-gradient';

const PostDetail: React.FC = () => {
  const navigation = useNavigation();
  const route = useRoute<RouteProp<RootStackParamList, 'PostDetail'>>();
  const { postId } = route.params;

  const post = useSelector((state: RootState) =>
    state.posts.find(p => p.id === postId)
  );

  if (!post) {
    return (
        <View style={[styles.centered, { flex: 1 }]}>
          <TouchableOpacity style={styles.sideButton} onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={24} color="#6846FF" />
          </TouchableOpacity>
          <Text>게시글을 찾을 수 없습니다.</Text>
        </View>
    );
  }

  return (
    <LinearGradient
      colors={['#FF87DD', '#B092FF', '#DBD6EC', '#F0F0E9']}
      locations={[0, 0.43, 0.71, 0.93]}
      start={{ x: 0, y: 0 }}
      end={{ x: 0, y: 0.35 }}
      style={styles.container}
    >
    
    <View style={styles.container}>
        <View style={styles.header}>
            <TouchableOpacity onPress={() => navigation.goBack()} style={styles.sideButton}>
                <Ionicons name="arrow-back" size={25} color="#fff" />
            </TouchableOpacity>
            <View style={styles.titleBox}>
                <Text style={styles.title}>라운지</Text>
            </View>
            <TouchableOpacity onPress={() => alert('수정화면으로 이동')} style={styles.sideButton}>
                <Ionicons name="create-outline" size={24} color="#fff" />
            </TouchableOpacity>
        </View>

      <ScrollView contentContainerStyle={styles.content}>

        <View style={styles.comment}>
            <Ionicons name="megaphone-outline" size={12} color="#3D3D3D" style={styles.icon} />
            <Text style={styles.commentText}>비적절한 게시글, 댓글은 신고 대상입니다.</Text>
        </View>

        <View style={styles.card}>
          <View style={styles.metaRow}>
            <Ionicons name="person-circle" size={40} color="#D8D8D8" style={{ marginRight: 3 }} />
            <Text style={styles.author}>{post.author}</Text>
            <Text style={styles.date}>{post.date}</Text>
            <Ionicons name="chatbubble-ellipses-outline" size={15} color="#B092FF" style={{ marginLeft: 12, marginRight: 2 }} />
            <Text style={styles.comments}>{post.comments}</Text>
            <Ionicons name="heart-outline" size={15} color="#FF6B81" style={{ marginLeft: 10, marginRight: 2 }} />
            <Text style={styles.likes}>{post.likes}</Text>
          </View>
          <Text style={styles.postTitle}>{post.title}</Text>
          <Text style={styles.text}>{post.text}</Text>
        </View>

        {/* 댓글 영역 (더미) */}
        <View style={styles.commentSection}>
          <Text style={styles.commentSectionTitle}>댓글</Text>
          <View style={styles.commentDivider} />
          <View style={styles.commentItem}>
            <Ionicons name="person-circle-outline" size={18} color="#B092FF" style={{ marginRight: 5 }} />
            <View>
              <Text style={styles.commentAuthor}>익명</Text>
              <Text style={styles.comment1}>좋은 정보 감사합니다!</Text>
            </View>
          </View>
          <View style={styles.commentDivider} />
          <View style={styles.commentItem}>
            <Ionicons name="person-circle-outline" size={18} color="#B092FF" style={{ marginRight: 5 }} />
            <View>
              <Text style={styles.commentAuthor}>익명</Text>
              <Text style={styles.comment1}>저도 궁금했어요~</Text>
            </View>
          </View>
        </View>
      </ScrollView>
        
    </View>

    
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 28,
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
    fontSize: 11,
    color: '#3D3D3D',
  },
  icon: {
    marginRight: 5,
    marginBottom: 2,
    color: '#3D3D3D',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    marginBottom: 24,
    height: 48,
    position: 'relative',
  },
  sideButton: {
    width: 40,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 2,
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 3 },
    shadowOpacity: 0.5,
    shadowRadius: 4,
    elevation: 5,
  },
  titleBox: {
    position: 'absolute',
    left: 0,
    right: 0,
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1,
    pointerEvents: 'none',
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: 'white',
    textAlign: 'center',
    textShadowColor: '#000',
    textShadowOffset: { width: 1, height: 2 },
    textShadowRadius: 4,
    elevation: 4, // Android
  },
  content: {
    paddingTop: 10,
    paddingHorizontal: 15,
    alignItems: 'center'
  },
  card: {
    width: '92%',
    marginBottom: 18,
    shadowColor: '#B092FF',
    shadowOpacity: 0.13,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 5,
  },
  postTitle: {
    fontSize: 16,
    fontWeight: '500',
    color: '#2C2C2C',
    marginBottom: 12,
    marginTop: 12,
    letterSpacing: 0.2,
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  author: {
    fontSize: 13,
    color: '#2C2C2C',
    marginRight: 10,
    fontWeight: '500',
  },
  date: {
    fontSize: 11,
    color: '#AAA',
    marginRight: 8,
  },
  comments: {
    fontSize: 12,
    color: '#B092FF',
    fontWeight: 'bold',
  },
  likes: {
    fontSize: 12,
    color: '#FF6B81',
    fontWeight: 'bold',
  },
  text: {
    fontSize: 15,
    color: '#444',
    lineHeight: 22,
  },
  commentSection: {
    width: '92%',
    backgroundColor: 'rgba(250,248,255,0.97)',
    borderRadius: 12,
    padding: 16,
    marginBottom: 20,
    alignSelf: 'center',
    shadowColor: '#B092FF',
    shadowOpacity: 0.06,
    shadowRadius: 3,
    elevation: 1,
  },
  commentSectionTitle: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#6846FF',
    marginBottom: 6,
    marginLeft: 2,
  },
  commentDivider: {
    height: 1,
    backgroundColor: '#E5C7A0',
    marginVertical: 8,
    opacity: 0.5,
  },
  commentItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 4,
  },
  commentAuthor: {
    fontSize: 12,
    color: '#6846FF',
    fontWeight: '600',
  },
  comment1: {
    fontSize: 13,
    color: '#444',
    marginTop: 2,
  },
  centered: {
    alignItems: 'center',
    justifyContent: 'center',
  },
});

export default PostDetail;
