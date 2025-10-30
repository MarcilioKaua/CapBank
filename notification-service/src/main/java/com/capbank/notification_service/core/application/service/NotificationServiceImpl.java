package com.capbank.notification_service.core.application.service;

import com.capbank.notification_service.core.domain.enums.NotificationStatus;
import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.in.NotificationUseCase;
import com.capbank.notification_service.core.ports.out.NotificationRepositoryPort;
import com.capbank.notification_service.core.ports.out.NotificationSenderPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationSenderPort notificationSender;

    public NotificationServiceImpl(NotificationRepositoryPort notificationRepository, NotificationSenderPort notificationSender) {
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
    }

    @Override
    public void processNotification(Notification notification) {
        notification.setId(UUID.randomUUID());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setDeliveryStatus(NotificationStatus.PENDING);

        notificationRepository.save(notification);

        try {
            notificationSender.send(notification);
            notification.setDeliveryStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setDeliveryStatus(NotificationStatus.ERROR);
        }

        notificationRepository.save(notification);
    }
}
