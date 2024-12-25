package com.ptit.datn.service;

import com.ptit.datn.constants.Constants;
import com.ptit.datn.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    // send notification to all users
    public void notify(String topic, String message) {
        messagingTemplate.convertAndSend(topic, message);
    }

    // send notification to list of users
    public void notifyUser(List<Long> userIds,String topic, String message) {
        for (Long id : userIds) {
            notifyUser(id, topic, message);
        }
    }
    // send notification to a user
    public void notifyUser(Long userId,String topic, String message) {
        if (DataUtils.isNullOrEmpty(userId)) {
            throw new RuntimeException("User id is required");
        }
        topic = topic + userId;
        messagingTemplate.convertAndSend(topic, message);
    }

}
