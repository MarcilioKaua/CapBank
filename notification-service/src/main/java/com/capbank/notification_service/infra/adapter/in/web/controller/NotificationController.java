package com.capbank.notification_service.infra.adapter.in.web.controller;

import com.capbank.notification_service.core.domain.enums.NotificationChannel;
import com.capbank.notification_service.core.domain.enums.NotificationStatus;
import com.capbank.notification_service.core.domain.enums.NotificationType;
import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.in.NotificationUseCase;
import com.capbank.notification_service.infra.adapter.in.web.dto.NotificationRequest;
import com.capbank.notification_service.infra.adapter.in.web.dto.NotificationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationUseCase notificationUseCase;

    public NotificationController(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request) {

        try {
            logger.info("Received notification request for user: {}, type: {}",
                       request.userId(), request.type());
    
            Notification notification = mapToNotification(request);

            notificationUseCase.processNotification(notification);

            logger.info("Notification processed successfully with ID: {}", notification.getId());

            NotificationResponse response = NotificationResponse.success(
                    notification.getId().toString(),
                    "Notification processed successfully"
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid notification request: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(NotificationResponse.error("Invalid request: " + e.getMessage()));

        } catch (Exception e) {
            logger.error("Error processing notification: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(NotificationResponse.error("Failed to process notification"));
        }
    }

    private Notification mapToNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUserId(UUID.fromString(request.userId()));
        notification.setRecipientEmail(request.recipientEmail());
        notification.setType(NotificationType.valueOf(request.type()));
        notification.setChannel(NotificationChannel.valueOf(request.channel()));
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setDeliveryStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());

        return notification;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
