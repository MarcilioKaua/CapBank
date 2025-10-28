package com.capbank.notification_service.infra.messaging;

import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.in.NotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitNotificationConsumer.class);

    private final NotificationUseCase notificationUseCase;

    public RabbitNotificationConsumer(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void receive(Notification notification) {
        logger.info("Received notification from RabbitMQ: userId={}, type={}",
                   notification.getUserId(), notification.getType());

        try {
            notificationUseCase.processNotification(notification);
            logger.info("Notification processed successfully from queue");
        } catch (Exception e) {
            logger.error("Error processing notification from queue: {}", e.getMessage(), e);
            throw e; 
        }
    }
}
