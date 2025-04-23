package top.javahe.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j 
@Component 
public class WebSocketHandler extends TextWebSocketHandler {
    // 使用线程安全的ConcurrentHashMap存储所有的WebSocket会话
    private static final ConcurrentHashMap<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 当新的WebSocket连接建立时,将会话存入Map
        SESSIONS.put(session.getId(), session);
        log.info("新的WebSocket连接建立: {}, 当前连接数: {}", session.getId(), SESSIONS.size());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 当收到客户端消息时,记录日志并回复确认消息
        log.info("收到消息: {}, 来自: {}", message.getPayload(), session.getId());
        session.sendMessage(new TextMessage("服务器已收到消息: " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 当WebSocket连接关闭时,从Map中移除会话
        SESSIONS.remove(session.getId());
        log.info("WebSocket连接关闭: {}, 状态: {}, 当前连接数: {}", 
            session.getId(), status.getCode(), SESSIONS.size());
    }

    // 群发消息的方法,遍历所有会话并发送消息
    // 这个方法可以被定时任务调用,比如每5秒发送一次服务器时间
    // 但是需要在另一个类中使用@Scheduled注解来实现定时任务
    public void sendMessageToAll(String message) {
        log.info("开始群发消息，当前连接数: {}", SESSIONS.size());
        SESSIONS.forEach((id, session) -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    log.debug("消息已发送到: {}", id);
                }
            } catch (IOException e) {
                log.error("发送消息失败: {}, 接收者: {}", e.getMessage(), id);
            }
        });
    }
}