package EtfRecommendService.etf.notification;

import java.time.LocalDateTime;

public record NotificationDto(
        String message,
        ReceiverType receiverType,
        NotificationType type,
        String targetId
) {

}
