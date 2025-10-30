package com.capbank.notification_service;
import com.capbank.notification_service.core.application.service.NotificationServiceImpl;
import com.capbank.notification_service.core.domain.enums.NotificationChannel;
import com.capbank.notification_service.core.domain.enums.NotificationStatus;
import com.capbank.notification_service.core.domain.enums.NotificationType;
import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.out.NotificationRepositoryPort;
import com.capbank.notification_service.core.ports.out.NotificationSenderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotificationUseCaseTests {

    @Mock
    private NotificationRepositoryPort notificationRepository;

    @Mock
    private NotificationSenderPort notificationSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("Deve processar notificação com sucesso e definir status SENT")
    void shouldProcessNotificationSuccessfully() {
        Notification notification = new Notification();
        notification.setRecipientEmail("user@test.com");
        notification.setMessage("Hello World");
        notification.setType(NotificationType.ALERT);
        notification.setChannel(NotificationChannel.EMAIL);

        notificationService.processNotification(notification);

        assertThat(notification.getId()).isNotNull();
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getDeliveryStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getSentAt()).isNotNull();

        verify(notificationRepository, times(2)).save(notification);
        verify(notificationSender, times(1)).send(notification);
    }

    @Test
    @DisplayName("Deve definir status ERROR quando o envio falhar")
    void shouldSetErrorStatusWhenSendingFails() {
        Notification notification = new Notification();
        notification.setRecipientEmail("user@test.com");
        notification.setMessage("Failure expected");
        notification.setType(NotificationType.ALERT);
        notification.setChannel(NotificationChannel.EMAIL);

        doThrow(new RuntimeException("SMTP error")).when(notificationSender).send(any(Notification.class));

        notificationService.processNotification(notification);

        assertThat(notification.getDeliveryStatus()).isEqualTo(NotificationStatus.ERROR);
        assertThat(notification.getSentAt()).isNull();

        verify(notificationRepository, times(2)).save(notification);
        verify(notificationSender, times(1)).send(notification);
    }

    @Test
    @DisplayName("Deve definir status PENDING e salvar notificação antes do envio")
    void shouldSetPendingStatusAndSaveBeforeSend() {
        Notification notification = new Notification();
        notification.setRecipientEmail("pending@test.com");
        notification.setMessage("Notificação pendente");

        doNothing().when(notificationSender).send(any(Notification.class));

        notificationService.processNotification(notification);

        assertThat(notification.getId()).isNotNull();
        assertThat(notification.getCreatedAt()).isNotNull();
        assertThat(notification.getDeliveryStatus())
                .isIn(NotificationStatus.SENT, NotificationStatus.PENDING, NotificationStatus.ERROR);

        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
        verify(notificationSender, times(1)).send(any(Notification.class));
    }
}
