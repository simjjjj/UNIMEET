package com.unimeet.backend.security;

import com.unimeet.backend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = getTokenFromHeaders(accessor);
            
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                try {
                    String username = jwtTokenProvider.getUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    accessor.setUser(authentication);
                    
                    // 세션에 사용자 정보 저장
                    accessor.getSessionAttributes().put("username", userDetails.getUsername());
                    accessor.getSessionAttributes().put("userId", ((com.unimeet.backend.domain.User) userDetails).getId());
                    
                    log.debug("WebSocket connection authenticated for user: {}", username);
                } catch (Exception e) {
                    log.error("WebSocket authentication failed", e);
                    throw new RuntimeException("Authentication failed");
                }
            } else {
                log.warn("WebSocket connection attempted without valid token");
                throw new RuntimeException("Authentication required");
            }
        }
        
        return message;
    }

    private String getTokenFromHeaders(StompHeaderAccessor accessor) {
        // Authorization 헤더에서 토큰 추출
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 쿼리 파라미터에서 토큰 추출 (fallback)
        String token = accessor.getFirstNativeHeader("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        
        return null;
    }
}