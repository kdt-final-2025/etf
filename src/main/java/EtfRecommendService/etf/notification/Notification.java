package EtfRecommendService.etf.notification;

import EtfRecommendService.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String userId; // 알림 받을 사용자 ID

    @NonNull
    private String message;

    @NonNull
    private LocalDateTime expiredTime;

    private boolean isRead = false;

}
