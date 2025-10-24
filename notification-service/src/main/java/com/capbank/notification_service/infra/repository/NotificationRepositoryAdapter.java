package com.capbank.notification_service.infra.repository;

import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.core.ports.out.NotificationRepositoryPort;
import com.capbank.notification_service.infra.exception.NotificationNotFoundException;
import com.capbank.notification_service.infra.mapper.NotificationMapper;
import com.capbank.notification_service.infra.repository.jpa.JpaNotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationRepositoryAdapter(JpaNotificationRepository jpaRepository, NotificationMapper notificationMapper) {
        this.jpaNotificationRepository = jpaRepository;
        this.notificationMapper = notificationMapper;
    }
    @Override
    public Notification save(Notification notification) {
        return notificationMapper.toDomain(jpaNotificationRepository.save(notificationMapper.toEntity(notification)));
    }

    @Override
    public List<Notification> findAll() {
        return jpaNotificationRepository.findAll().stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Notification findById(UUID id) {
        return jpaNotificationRepository.findById(id)
                .map(notificationMapper::toDomain)
                .orElseThrow(() -> new NotificationNotFoundException("Notificação não encontrada"));
    }
}