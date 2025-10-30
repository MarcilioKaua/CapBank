package com.capbank.notification_service.infra.mapper;

import com.capbank.notification_service.core.domain.model.Notification;
import com.capbank.notification_service.infra.entity.NotificationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-29T16:53:38-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Microsoft)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationEntity toEntity(Notification domain) {
        if ( domain == null ) {
            return null;
        }

        NotificationEntity notificationEntity = new NotificationEntity();

        notificationEntity.setId( domain.getId() );
        notificationEntity.setUserId( domain.getUserId() );
        notificationEntity.setRecipientEmail( domain.getRecipientEmail() );
        notificationEntity.setType( domain.getType() );
        notificationEntity.setTitle( domain.getTitle() );
        notificationEntity.setMessage( domain.getMessage() );
        notificationEntity.setChannel( domain.getChannel() );
        notificationEntity.setDeliveryStatus( domain.getDeliveryStatus() );
        notificationEntity.setCreatedAt( domain.getCreatedAt() );
        notificationEntity.setSentAt( domain.getSentAt() );

        return notificationEntity;
    }

    @Override
    public Notification toDomain(NotificationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Notification notification = new Notification();

        notification.setId( entity.getId() );
        notification.setUserId( entity.getUserId() );
        notification.setRecipientEmail( entity.getRecipientEmail() );
        notification.setType( entity.getType() );
        notification.setTitle( entity.getTitle() );
        notification.setMessage( entity.getMessage() );
        notification.setChannel( entity.getChannel() );
        notification.setDeliveryStatus( entity.getDeliveryStatus() );
        notification.setCreatedAt( entity.getCreatedAt() );
        notification.setSentAt( entity.getSentAt() );

        return notification;
    }
}
