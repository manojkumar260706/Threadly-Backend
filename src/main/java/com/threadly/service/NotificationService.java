package com.threadly.service;

import com.threadly.entity.Notification;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.NotificationRepository;
import com.threadly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(User toSend, String message) {
        Notification notification = new Notification();
        notification.setRecipient(toSend);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ApiException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }
}
