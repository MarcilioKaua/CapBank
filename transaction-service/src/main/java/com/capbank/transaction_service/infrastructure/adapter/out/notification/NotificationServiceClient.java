package com.capbank.transaction_service.infrastructure.adapter.out.notification;

import com.capbank.transaction_service.core.application.port.out.NotificationServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class NotificationServiceClient implements NotificationServicePort {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public NotificationServiceClient(
            RestTemplate restTemplate,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory,
            @Value("${services.notification.url:http://localhost:8086}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    @Override
    public boolean sendTransactionNotification(TransactionNotification notification) {
        try {
            logger.info("Sending notification to user: {} for transaction: {}",
                       notification.userId(), notification.transactionData().transactionId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            NotificationRequest request = mapToRequest(notification);
            HttpEntity<NotificationRequest> httpEntity = new HttpEntity<>(request, headers);

            String url = notificationServiceUrl + "/api/notifications";
            CircuitBreaker cb = circuitBreakerFactory.create("transactionNotificationClient");
            Boolean success = cb.run(() -> {
                ResponseEntity<NotificationResponse> response = restTemplate.postForEntity(
                        url, httpEntity, NotificationResponse.class);
                boolean ok = response.getStatusCode().is2xxSuccessful();
                if (ok) {
                    logger.info("Notification sent successfully to user: {}", notification.userId());
                } else {
                    logger.warn("Failed to send notification. Status: {}", response.getStatusCode());
                }
                return ok;
            }, throwable -> {
                logger.error("Circuit breaker fallback - notification send failed for user {}: {}",
                        notification.userId(), throwable.getMessage());
                return false;
            });

            return success != null && success;

        } catch (Exception e) {
            logger.error("Error sending notification to user {}: {}",
                        notification.userId(), e.getMessage(), e);


            return handleNotificationFailure(notification, e);
        }
    }

    private NotificationRequest mapToRequest(TransactionNotification notification) {
        return new NotificationRequest(
                notification.userId(),
                null,
                notification.accountId().toString(),
                notification.type().name(),
                notification.channel().name(),
                notification.title(),
                notification.message(),
                notification.transactionData()
        );
    }

    private boolean handleNotificationFailure(TransactionNotification notification, Exception e) {
        
        logger.error("Notification failure details - User: {}, Type: {}, Error: {}",
                    notification.userId(), notification.type(), e.getMessage());

        
        logFailureForMonitoring(notification, e);

        return false; 
    }

    private void logFailureForMonitoring(TransactionNotification notification, Exception e) {
       
        logger.warn("NOTIFICATION_FAILURE: user={}, type={}, account={}, transaction={}, error={}",
                   notification.userId(),
                   notification.type(),
                   notification.accountId(),
                   notification.transactionData().transactionId(),
                   e.getClass().getSimpleName());
    }

 
    public record NotificationRequest(
            String userId,
            String recipientEmail,
            String accountId,
            String type,
            String channel,
            String title,
            String message,
            TransactionMetadata transactionData
    ) {}

 
    public record NotificationResponse(
            String id,
            String status,
            String message
    ) {}
}