import React, { useState, useRef } from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity, KeyboardAvoidingView, Platform } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import GradientScreen from '../../component/GradientScreen';
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import type { RootStackParamList } from '../../navigation/types';

const dummyMessages = [
  { id: 1, text: '안녕하세요!', mine: false, time: '오후 2:01' },
  { id: 2, text: '안녕하세요~', mine: true, time: '오후 2:01' },
  { id: 3, text: 'React Native 스터디 내일 7시에 만나요!', mine: false, time: '오후 2:02' },
  { id: 4, text: '네! 장소는 어디에요?', mine: true, time: '오후 2:02' },
];

const ChatRoom: React.FC = () => {
  const navigation = useNavigation();
  // 타입 명확히 지정
  const route = useRoute<RouteProp<RootStackParamList, 'ChatRoom'>>();
  const { roomId } = route.params;

  // store에서 채팅방 정보 가져오기
  const room = useSelector((state: RootState) =>
    state.chats.find(r => r.id === roomId)
  );

  const [messages, setMessages] = useState(dummyMessages);
  const [input, setInput] = useState('');
  const scrollRef = useRef<ScrollView>(null);

  const handleSend = () => {
    if (!input.trim()) return;
    setMessages([
      ...messages,
      { id: messages.length + 1, text: input, mine: true, time: '오후 2:03' },
    ]);
    setInput('');
    setTimeout(() => scrollRef.current?.scrollToEnd({ animated: true }), 100);
  };

  return (
    <GradientScreen>
      <View style={chatStyles.header}>
        <TouchableOpacity style={chatStyles.sideButton} onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={25} color="#fff" />
        </TouchableOpacity>
        <View style={chatStyles.titleBox}>
          <Text style={chatStyles.title}>{room?.name || '채팅방'}</Text>
        </View>
        <View style={{ width: 40 }} />
      </View>

      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 70 : 0}
      >
        <ScrollView
          ref={scrollRef}
          contentContainerStyle={chatStyles.messages}
          showsVerticalScrollIndicator={false}
          onContentSizeChange={() => scrollRef.current?.scrollToEnd({ animated: true })}
        >
          {messages.map(msg => (
            <View
              key={msg.id}
              style={[
                chatStyles.messageRow,
                msg.mine ? chatStyles.myRow : chatStyles.otherRow,
              ]}
            >
              <View style={[chatStyles.bubble, msg.mine ? chatStyles.myBubble : chatStyles.otherBubble]}>
                <Text style={chatStyles.bubbleText}>{msg.text}</Text>
                <Text style={chatStyles.bubbleTime}>{msg.time}</Text>
              </View>
            </View>
          ))}
        </ScrollView>
        <View style={chatStyles.inputBar}>
          <TextInput
            style={chatStyles.input}
            value={input}
            onChangeText={setInput}
            placeholder="메시지를 입력하세요"
            placeholderTextColor="#AAA"
          />
          <TouchableOpacity style={chatStyles.sendBtn} onPress={handleSend}>
            <Ionicons name="send" size={20} color="#fff" />
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </GradientScreen>
  );
};

const chatStyles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    marginBottom: 11,
    height: 48,
    position: 'relative',
    marginTop: 60,
  },
  sideButton: {
    width: 40,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 2,
  },
  titleBox: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'flex-start', // 왼쪽 정렬
    paddingLeft: 0,
    flex: 1,
  },
  title: {
    fontSize: 17,
    fontWeight: '600',
    color: 'white',
    textAlign: 'left',
    textShadowColor: '#000',
    textShadowOffset: { width: 1, height: 2 },
    textShadowRadius: 4,
    elevation: 4,
  },
  messages: {
    flexGrow: 1,
    justifyContent: 'flex-end',
    padding: 16,
    paddingBottom: 8,
  },
  messageRow: {
    flexDirection: 'row',
    marginBottom: 10,
  },
  myRow: {
    justifyContent: 'flex-end',
  },
  otherRow: {
    justifyContent: 'flex-start',
  },
  bubble: {
    maxWidth: '75%',
    borderRadius: 18,
    paddingVertical: 10,
    paddingHorizontal: 14,
    marginHorizontal: 4,
    shadowColor: '#B092FF',
    shadowOpacity: 0.08,
    shadowRadius: 3,
    elevation: 1,
  },
  myBubble: {
    backgroundColor: '#6846FF',
    alignSelf: 'flex-end',
  },
  otherBubble: {
    backgroundColor: '#F2F2F2',
    alignSelf: 'flex-start',
  },
  bubbleText: {
    fontSize: 14,
    color: '#222',
  },
  bubbleTime: {
    fontSize: 10,
    color: '#AAA',
    alignSelf: 'flex-end',
    marginTop: 4,
  },
  inputBar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: '#EEE',
  },
  input: {
    flex: 1,
    fontSize: 15,
    paddingVertical: 6,
    paddingHorizontal: 12,
    backgroundColor: '#F8F8F8',
    borderRadius: 18,
    marginRight: 10,
    color: '#222',
  },
  sendBtn: {
    backgroundColor: '#6846FF',
    borderRadius: 18,
    padding: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
});

export default ChatRoom;