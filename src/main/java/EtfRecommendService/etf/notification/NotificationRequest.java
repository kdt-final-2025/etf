package EtfRecommendService.etf.notification;

import java.time.LocalDateTime;

public record NotificationRequest(
        Long userId,
        String message,
        LocalDateTime expiredTime
) {
}
