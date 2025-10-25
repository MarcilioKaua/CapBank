package com.capbank.notification_service.core.ports.out;

import com.capbank.notification_service.core.domain.model.Notification;

public interface NotificationSenderPort {
    void send(Notification notification);
}
