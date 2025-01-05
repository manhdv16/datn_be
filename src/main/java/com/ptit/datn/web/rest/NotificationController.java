package com.ptit.datn.web.rest;

import com.ptit.datn.constants.Constants;
import com.ptit.datn.domain.Notification;
import com.ptit.datn.dto.response.ApiResponse;
import com.ptit.datn.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/test")
    public String notifyTest(@RequestParam Long userId) {
        notificationService.notifyUser(userId,Constants.TOPIC.REQUEST ,"A new request has been created!");
        return "Notification sent!";
    }
    @GetMapping("/read/{userId}")
    public ApiResponse readNotification(@PathVariable("userId") Long userId) {
        notificationService.readNotification(userId);
        return ApiResponse.builder()
            .message("Notification read!")
            .build();
    }
    @GetMapping("/{userId}")
    public Page<Notification> getAll(@PathVariable("userId") Long userId, Pageable pageable) {
        return notificationService.getAll(userId, pageable);
    }
    @GetMapping("/unread/{userId}")
    public Page<Notification> getAllNotiUnRead(@PathVariable("userId") Long userId, Pageable pageable) {
        return notificationService.getAllNotiUnRead(userId, pageable);
    }
}
