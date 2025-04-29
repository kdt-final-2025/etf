package EtfRecommendService.etf.notification;

import EtfRecommendService.etf.SubscribeRepository;
import EtfRecommendService.etf.domain.Etf;
import EtfRecommendService.etf.domain.Subscribe;
import EtfRecommendService.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class NotificationService {


    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final SubscribeRepository subscribeRepository;

    public SseEmitter createEmitter(String emitterId) {
        SseEmitter emitter = new SseEmitter(0L); // timeout 없음
        emitters.put(emitterId, emitter);

        emitter.onCompletion(() -> emitters.remove(emitterId));
        emitter.onTimeout(() -> emitters.remove(emitterId));
        emitter.onError(e -> emitters.remove(emitterId));
        return emitter;
    }

    public void sendNotificationToUser(NotificationRequest request, NotificationDto data) {
        SseEmitter emitter = emitters.get(request.userId());

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("newPost")
                        .data(data));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(String.valueOf(request.userId()));
                saveNotification(request.userId(), data);
            }
        } else {
            saveNotification(request.userId(), data);
        }
    }

    private void saveNotification(Long userId, NotificationDto data) {
        Notification notification = new Notification(userId, data.message(), data.expiredTime());
        notificationRepository.save(notification);
    }

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 실행
    @Transactional
    public void notifyExpiringSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);

        // 하루 뒤 만료될 구독 찾기
        List<Subscribe> expiringSubscriptions = subscribeRepository.findAllByExpiredTimeBetween(
                oneDayLater.withHour(0).withMinute(0).withSecond(0),
                oneDayLater.withHour(23).withMinute(59).withSecond(59)
        );

        for (Subscribe subscribe : expiringSubscriptions) {
            User user = subscribe.getUser();
            Etf etf = subscribe.getEtf();

            // 알림 내용 생성
            String message = etf.getEtfName() + " ETF 구독이 하루 후 만료됩니다.";
            LocalDateTime expiredTime = subscribe.getExpiredTime();

            NotificationDto notificationDto = new NotificationDto(message, expiredTime);
            NotificationRequest notificationRequest = new NotificationRequest(user.getId(), message, expiredTime);

            // 알림 전송
            sendNotificationToUser(notificationRequest, notificationDto);
        }
    }



    /*public long countUnreadNotifications(String emitterId) {
        return notificationRepository.countByUserIdAndIsReadFalse(emitterId);
    }

    public void markAllAsRead(String emitterId) {
        notificationRepository.markAllAsRead(emitterId);
    }*/

}

