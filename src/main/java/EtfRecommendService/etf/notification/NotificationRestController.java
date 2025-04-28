package EtfRecommendService.etf.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping("/sse/notifications")
    public SseEmitter streamNotifications(@RequestParam String emitterId) {
        System.out.println("연결 요청: " + emitterId);
        return notificationService.createEmitter(emitterId);
    }
}
