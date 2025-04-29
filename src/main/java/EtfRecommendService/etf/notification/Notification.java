package EtfRecommendService.etf.notification;

import EtfRecommendService.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // 알림 받을 사용자 ID

    private String message;

    private LocalDateTime expiredTime;

    private boolean isRead = false;

    public Notification(String userId, String message, LocalDateTime expiredTime) {
        this.userId = userId;
        this.message = message;
        this.expiredTime = expiredTime;
    }
}
