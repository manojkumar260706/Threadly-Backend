package com.threadly.controller;

import com.threadly.entity.Notification;
import com.threadly.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Get all Notifications of a user")
    @GetMapping
    public List<Notification> getNotifications(Authentication authentication) {
        return notificationService.getNotifications(authentication.getName());
    }
}
