package com.capbank.notification_service.infra.repository.jpa;

import com.capbank.notification_service.infra.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {
}
