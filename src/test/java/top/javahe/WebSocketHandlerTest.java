package top.javahe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import top.javahe.config.WebSocketHandler;

import java.util.UUID;

import static org.mockito.Mockito.*;

class WebSocketHandlerTest {

    private WebSocketHandler webSocketHandler;
    private WebSocketSession session;
    private String sessionId;

    @BeforeEach
    void setUp() {
        webSocketHandler = new WebSocketHandler();
        session = mock(WebSocketSession.class);
        sessionId = UUID.randomUUID().toString(); // 使用随机生成的UUID作为会话ID
        // 这行代码使用了Mockito框架的when().thenReturn()方法来模拟(mock)行为
        // when(session.getId()) - 当调用mock对象session的getId()方法时
        // thenReturn(sessionId) - 返回我们预设的sessionId值
        // 这样做的目的是:
        // 1. session是一个mock对象,默认所有方法返回null
        // 2. 我们需要让它的getId()方法返回一个特定的值用于测试
        // 3. 通过这行代码,我们告诉mock对象在调用getId()时返回之前生成的UUID
        when(session.getId()).thenReturn(sessionId);
    }

    @Test
    void testConnectionLifecycle() throws Exception {
        // 测试连接建立
        webSocketHandler.afterConnectionEstablished(session);
        verify(session, times(2)).getId(); // 连接建立时调用2次
        verify(session, never()).sendMessage(any(TextMessage.class)); // 确保连接建立时没有发送消息

        // 测试消息处理
        TextMessage message = new TextMessage("测试消息");
        webSocketHandler.handleTextMessage(session, message);
        verify(session, times(3)).getId(); // 消息处理时调用1次，总共3次
        verify(session, times(1)).sendMessage(any(TextMessage.class)); // 确保发送了响应消息

        // 测试连接关闭
        webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);
        verify(session, times(5)).getId(); // 连接关闭时调用2次，总共5次
    }

    @Test
    void testSendMessageToAll() throws Exception {
        // 建立连接
        webSocketHandler.afterConnectionEstablished(session);
        when(session.isOpen()).thenReturn(true);

        // 测试群发消息
        String broadcastMessage = "群发测试消息";
        webSocketHandler.sendMessageToAll(broadcastMessage);
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testMultipleConnections() throws Exception {
        // 创建多个会话
        WebSocketSession session2 = mock(WebSocketSession.class);
        String sessionId2 = UUID.randomUUID().toString();
        when(session2.getId()).thenReturn(sessionId2);
        when(session2.isOpen()).thenReturn(true); // 添加这行，设置session2为打开状态

        // 建立连接
        webSocketHandler.afterConnectionEstablished(session);
        webSocketHandler.afterConnectionEstablished(session2);
        when(session.isOpen()).thenReturn(true); // 添加这行，设置session为打开状态

        // 测试群发消息
        String broadcastMessage = "群发测试消息";
        webSocketHandler.sendMessageToAll(broadcastMessage);

        // 验证两个会话都收到了消息
        verify(session, times(1)).sendMessage(any(TextMessage.class));
        verify(session2, times(1)).sendMessage(any(TextMessage.class));
    }
}