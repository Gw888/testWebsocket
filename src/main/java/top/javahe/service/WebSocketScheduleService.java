package top.javahe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.javahe.config.WebSocketHandler;
import top.javahe.util.SystemResourceUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WebSocketScheduleService {

    private final WebSocketHandler webSocketHandler;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void sendSystemInfo() {
        String currentTime = LocalDateTime.now().format(formatter);
        String systemInfo = SystemResourceUtil.getSystemInfo();
        String message = String.format("服务器时间：%s\n%s", currentTime, systemInfo);
        webSocketHandler.sendMessageToAll(message);
    }
}