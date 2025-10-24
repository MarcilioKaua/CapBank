package com.capbank.notification_service.infra.mapper;

import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.infra.entity.NotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationEntity toEntity(Notification domain);
    Notification toDomain(NotificationEntity entity);
}
