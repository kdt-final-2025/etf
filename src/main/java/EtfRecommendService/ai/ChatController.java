package EtfRecommendService.ai;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//https://localhost:8443/api/chat
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req) {
        try {
            ChatResponse resp = service.generate(req);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new ChatResponse("오류 발생: " + e.getMessage()));
        }
    }
}