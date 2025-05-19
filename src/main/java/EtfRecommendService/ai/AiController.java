package EtfRecommendService.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // (간단 예시) 메모리 상에 히스토리 보관
    private final StringBuilder history = new StringBuilder();



    @PostMapping
    public Mono<ResponseEntity<String>> chat(@RequestBody Map<String,String> body) {
        String userInput = body.get("message");
        // 키워드 필터링
        if (!userInput.matches(".*(주식|증시|코스피|코스닥|환율|금리|경제|매수|매도).*")) {
            return Mono.just(ResponseEntity
                    .badRequest()
                    .body("이 챗봇은 경제·주식 관련 질문에만 답변합니다."));
        }

        history.append("User: ").append(userInput).append("\n");
        return aiService.ask(history.toString(), userInput)
                .map(reply -> {
                    history.append("Assistant: ").append(reply).append("\n");
                    return ResponseEntity.ok(reply);
                });
    }
}
