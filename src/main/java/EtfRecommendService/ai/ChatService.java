package EtfRecommendService.ai;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ChatService {

    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${generative.language.api.key}")
    private String apiKey;

    private static final String ENDPOINT_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s";

    public ChatResponse generate(ChatRequest req) throws IOException, InterruptedException {
        // 시스템 지시문 + 히스토리 구성
        StringBuilder history = new StringBuilder();
        history.append("System: 이 챗봇은 오직 경제 및 주식 시장 관련 질문에만 답변합니다. ")
                .append("그 외 주제의 질문에는 정중히 거절하세요.\n")
                .append(req.history()).append("\n")
                .append("User: ").append(req.userInput()).append("\n");

        String jsonPayload = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "%s" }
                  ]
                }
              ]
            }
            """.formatted(escapeJson(history.toString()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(ENDPOINT_TEMPLATE, apiKey)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("API 호출 에러: HTTP " + resp.statusCode());
        }

        String assistantReply = parseTextFromResponse(resp.body());
        return new ChatResponse(assistantReply);
    }

    // JSON 특수문자 이스케이프
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    // 응답 본문에서 "text" 필드만 추출
    private static String parseTextFromResponse(String body) {
        int idx = body.indexOf("\"text\":");
        if (idx < 0) return body;
        int start = body.indexOf("\"", idx + 7) + 1;
        int end = body.indexOf("\"", start);
        if (start < 0 || end < 0) return body;
        return body.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");
    }
}