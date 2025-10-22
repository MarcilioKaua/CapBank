package com.capbank.notification_service.infra.sender;

import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.out.NotificationSenderPort;
import com.capbank.notification_service.infra.exception.NotificationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderAdapter implements NotificationSenderPort {

    private final JavaMailSender mailSender;

    public EmailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo();
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());
            mailSender.send(message);
        } catch (Exception e) {
            throw new NotificationException("Erro ao enviar email");
        }
    }
}
