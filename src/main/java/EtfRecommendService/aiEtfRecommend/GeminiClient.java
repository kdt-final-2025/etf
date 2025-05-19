package EtfRecommendService.aiEtfRecommend;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;


@Component
public class GeminiClient {
    @Value("${generative.language.api.key}")
    private String apiKey;

    private static final String ENDPOINT_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Recommendation> fetchRecommendations(QuestionnaireRequest req) throws IOException, InterruptedException {
        String prompt = buildPrompt(req);
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );
        String jsonPayload = objectMapper.writeValueAsString(body);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format(ENDPOINT_TEMPLATE, apiKey)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error: HTTP " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode textNode = root
                .path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text");
        String recJson = textNode.asText();

        // 마크다운 코드펜스(```) 또는 백틱 제거
        recJson = recJson.trim();
        if (recJson.startsWith("```")) {
            int firstNewline = recJson.indexOf('\n');
            int lastFence = recJson.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                recJson = recJson.substring(firstNewline + 1, lastFence).trim();
            }
        }

        return objectMapper.readValue(recJson, new TypeReference<List<Recommendation>>() {
        });
    }

    private String buildPrompt(QuestionnaireRequest req) {
        String[] questions = {
                "1. 왜 투자하려고 하나요?",
                "2. 얼마나 오래 넣어둘 계획인가요?",
                "3. 얼마를 투자할 수 있나요?",
                "4. 가격이 오르내릴 때 기분이 어떤가요?",
                "5. 투자해 본 경험이 있나요?",
                "6. 연 수익을 어느 정도 기대하나요?",
                "7. 어떤 종류 ETF를 원하나요?",
                "8. 어느 나라나 분야에 관심 있나요?",
                "9. 착한 투자(환경·사회) 관심은 어느 정도인가요?",
                "10. 언제든 사고팔고 싶은가요? 아니면 오래 두고 싶은가요?"
        };
        String[] answers = {
                req.goal(),
                req.duration(),
                req.amount(),
                req.feeling(),
                req.experience(),
                req.expectedReturn(),
                req.etfType(),
                req.region(),
                req.esgInterest(),
                req.tradingFrequency()
        };

        StringBuilder sb = new StringBuilder();
        sb.append("아래 설문 문항과 사용자의 답변을 기반으로 ETF 3종을 JSON 배열로 추천해 주세요. 각 객체에 symbol, name, reason을 포함해주세요.\n\n");
        for (int i = 0; i < questions.length; i++) {
            sb.append(questions[i]).append(" : ").append(answers[i]).append("\n");
        }
        return sb.toString();
    }
}
