package com.capbank.notification_service.core.ports.out;

import com.capbank.notification_service.core.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    List<Notification> findAll();
    Notification findById(UUID id);
}
