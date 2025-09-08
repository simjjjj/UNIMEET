import { configureStore } from '@reduxjs/toolkit';
import profileReducer from './profileSlice';
import postsReducer from './postsSlice';
import chatsReducer from './chatsSlice';
import commentsReducer from './commentsSlice';
import authReducer from './authSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    posts: postsReducer,
    profile: profileReducer,
    chats: chatsReducer,
    comments: commentsReducer,
  }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;