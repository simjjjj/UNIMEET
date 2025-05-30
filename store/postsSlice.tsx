import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface Post {
  id: number;
  title: string;
  text?: string;
  author: string;
  date: string;
  comments: number;
  likes: number;
  notice?: boolean;
}

const initialState: Post[] = [
  {
    id: 1,
    title: '오늘 점메추좀',
    text: '점심 메뉴를 도저히 못 고르겠어요',
    author: '익명',
    date: '5/30 20:05',
    comments: 4,
    likes: 3,
  },
  {
    id: 2,
    title: '스터디 모집합니다! (React Native)',
    text: '어플개발 고수 가보자',
    author: '익명',
    date: '5/30 19:47',
    comments: 2,
    likes: 1,
  },
  {
    id: 3,
    title: '어제 보름달',
    text: '어제 완전 보름달이고 날도 맑고 달도 밝았다던데 사진 찍으신 분 있으신가요 ㅜㅜ',
    author: '익명',
    date: '5/30 19:35',
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

const postsSlice = createSlice({
  name: 'posts',
  initialState,
  reducers: {
    addPost(state, action: PayloadAction<Post>) {
      state.push(action.payload);
    },
    updatePost(state, action: PayloadAction<Post>) {
      const index = state.findIndex(post => post.id === action.payload.id);
      if (index !== -1) {
        state[index] = action.payload;
      }
    },
    deletePost(state, action: PayloadAction<number>) {
      return state.filter(post => post.id !== action.payload);
    },
  },
});

export const { addPost, updatePost, deletePost } = postsSlice.actions;
export default postsSlice.reducer;