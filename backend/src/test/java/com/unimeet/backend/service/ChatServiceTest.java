package com.unimeet.backend.service;

import com.unimeet.backend.domain.ChatMessage;
import com.unimeet.backend.domain.ChatRoom;
import com.unimeet.backend.repository.ChatMessageRepository;
import com.unimeet.backend.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    private ChatRoom chatRoom;
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatRoom = ChatRoom.builder()
                .id("room123")
                .participantIds(List.of("userA", "userB"))
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        chatMessage = ChatMessage.builder()
                .id("message123")
                .roomId("room123")
                .sender("User A")
                .senderId("userA")
                .content("안녕하세요!")
                .type(ChatMessage.MessageType.CHAT)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();
    }

    @Test
    @DisplayName("채팅방 생성 - 새로운 채팅방")
    void getOrCreateChatRoom_NewRoom() {
        // given
        given(chatRoomRepository.findByParticipantIdsContainingAllIgnoreOrder(
                List.of("userA", "userB"))).willReturn(Optional.empty());
        given(chatRoomRepository.save(any(ChatRoom.class))).willReturn(chatRoom);

        // when
        ChatRoom result = chatService.getOrCreateChatRoom("userA", "userB");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getParticipantIds()).containsExactlyInAnyOrder("userA", "userB");
        assertThat(result.isActive()).isTrue();
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 조회 - 기존 채팅방")
    void getOrCreateChatRoom_ExistingRoom() {
        // given
        given(chatRoomRepository.findByParticipantIdsContainingAllIgnoreOrder(
                List.of("userA", "userB"))).willReturn(Optional.of(chatRoom));

        // when
        ChatRoom result = chatService.getOrCreateChatRoom("userA", "userB");

        // then
        assertThat(result).isEqualTo(chatRoom);
        verify(chatRoomRepository).findByParticipantIdsContainingAllIgnoreOrder(List.of("userA", "userB"));
    }

    @Test
    @DisplayName("메시지 저장 성공")
    void saveMessage_Success() {
        // given
        ChatMessage messageToSave = ChatMessage.builder()
                .roomId("room123")
                .sender("User A")
                .senderId("userA")
                .content("안녕하세요!")
                .type(ChatMessage.MessageType.CHAT)
                .build();

        given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(chatMessage);

        // when
        ChatMessage result = chatService.saveMessage(messageToSave);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.isRead()).isFalse();
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("채팅방 메시지 목록 조회")
    void getChatMessages_Success() {
        // given
        List<ChatMessage> messages = List.of(chatMessage);
        given(chatMessageRepository.findByRoomIdOrderByTimestampAsc("room123")).willReturn(messages);

        // when
        List<ChatMessage> result = chatService.getChatMessages("room123");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(chatMessage);
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회")
    void getUserChatRooms_Success() {
        // given
        List<ChatRoom> chatRooms = List.of(chatRoom);
        given(chatRoomRepository.findByParticipantIdsContainingAndActiveTrue("userA")).willReturn(chatRooms);

        // when
        List<ChatRoom> result = chatService.getUserChatRooms("userA");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(chatRoom);
    }

    @Test
    @DisplayName("메시지 읽음 처리")
    void markMessagesAsRead_Success() {
        // given
        List<ChatMessage> unreadMessages = List.of(chatMessage);
        given(chatMessageRepository.findByRoomIdAndReadFalseAndSenderIdNot("room123", "userB"))
                .willReturn(unreadMessages);

        // when
        chatService.markMessagesAsRead("room123", "userB");

        // then
        verify(chatMessageRepository).findByRoomIdAndReadFalseAndSenderIdNot("room123", "userB");
        verify(chatMessageRepository).saveAll(unreadMessages);
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 조회")
    void getUnreadMessageCount_Success() {
        // given
        given(chatMessageRepository.countByRoomIdAndReadFalseAndSenderIdNot("room123", "userB"))
                .willReturn(5L);

        // when
        long result = chatService.getUnreadMessageCount("room123", "userB");

        // then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    @DisplayName("채팅방 비활성화")
    void deactivateChatRoom_Success() {
        // given
        given(chatRoomRepository.findById("room123")).willReturn(Optional.of(chatRoom));

        // when
        chatService.deactivateChatRoom("room123");

        // then
        verify(chatRoomRepository).findById("room123");
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }
}