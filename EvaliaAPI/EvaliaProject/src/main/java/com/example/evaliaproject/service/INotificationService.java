package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Notification;
import com.example.evaliaproject.entity.NotificationType;
import com.example.evaliaproject.entity.User;

import java.util.List;

public interface INotificationService {
     void notify(User recipient, Announce ann, String message, NotificationType type);
     List<Notification> listForUser(Long recipientId);
     void markSeen(String id);
     void markAllSeen(Long recipientId);


    ////

}
