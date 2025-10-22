package com.capbank.notification_service.infra.messaging;

import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.in.NotificationUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitNotificationConsumer {

    private final NotificationUseCase notificationUseCase;

    public RabbitNotificationConsumer(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @RabbitListener(queues = "notification.queue")
    public void receive(Notification notification) {
        notificationUseCase.processNotification(notification);
    }
}
