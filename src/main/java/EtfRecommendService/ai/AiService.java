package EtfRecommendService.ai;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AiService {


    private final WebClient webClient;
    private final String systemPrompt =
            "System: 이 챗봇은 오직 경제 및 주식 시장 관련 질문에만 답변합니다. "
                    + "그 외 주제의 질문에는 정중히 거절하세요.\n";

    public AiService(WebClient geminiWebClient) {
        this.webClient = geminiWebClient;
    }

    public Mono<String> ask(String history, String userInput) {
        // 히스토리+시스템 프롬프트 결합
        String fullHistory = systemPrompt + history
                + "User: " + userInput + "\n";
        String payload = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": "%s" }
                      ]
                    }
                  ]
                }
                """.formatted(escapeJson(fullHistory));

        return webClient.post()
                // ?key={key} 가 자동으로 붙습니다 (config에서)
                .uri(uriBuilder -> uriBuilder.queryParam("key", "{key}").build())
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseTextFromResponse);
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    private String parseTextFromResponse(String body) {
        int idx = body.indexOf("\"text\":");
        if (idx < 0) return body;
        int start = body.indexOf("\"", idx + 7) + 1;
        int end = body.indexOf("\"", start);
        return start < 0 || end < 0 ? body
                : body.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");
    }


}
