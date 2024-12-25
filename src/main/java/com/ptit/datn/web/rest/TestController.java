package com.ptit.datn.web.rest;

import com.ptit.datn.constants.Constants;
import com.ptit.datn.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final NotificationService notificationService;

    @GetMapping("")
    public String notifyTest(@RequestParam Long userId) {
        notificationService.notifyUser(userId,Constants.TOPIC.REQUEST ,"A new request has been created!");
        return "Notification sent!";
    }
}
