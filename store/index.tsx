import { configureStore } from '@reduxjs/toolkit';
import profileReducer from './profileSlice';
import postsReducer from './postsSlice';
import chatsReducer from './ChatsSlice';

export const store = configureStore({
  reducer: {
    posts: postsReducer,
    profile: profileReducer,
    chats: chatsReducer,
  }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;