package com.capbank.notification_service.core.ports.in;

import com.capbank.notification_service.core.domain.model.Notification;

public interface NotificationUseCase {
    void processNotification(Notification notification);
}
