package com.ptit.datn.service;

import com.ptit.datn.constants.Constants;
import com.ptit.datn.domain.Notification;
import com.ptit.datn.repository.NotificationRepository;
import com.ptit.datn.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final NotificationRepository notificationRepository;

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
        Notification notification = Notification.builder()
            .userId(userId)
            .topic(topic)
            .message(message)
            .status(Constants.NOTIFICATION_STATUS.UNREAD)
            .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend(topic, message);
    }

    // mark notification as read
    public void readNotification(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdAndStatus(userId, Constants.NOTIFICATION_STATUS.UNREAD);
        for (Notification notification : notifications) {
            notification.setStatus(Constants.NOTIFICATION_STATUS.READ);
            notificationRepository.save(notification);
        }
    }

    // get all notifications of a user
    public Page<Notification> getAll(Long userId, Pageable pageable) {
        return notificationRepository.findAllByUserId(userId, pageable);
    }

    // get all notifications unread of a user
    public Page<Notification> getAllNotiUnRead(Long userId, Pageable pageable) {
        return notificationRepository.findAllByUserIdAndStatus(userId, Constants.NOTIFICATION_STATUS.UNREAD, pageable);
    }

}
