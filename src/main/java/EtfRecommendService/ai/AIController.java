package EtfRecommendService.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AIController {
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;

    @GetMapping("/chat")
    public Map<String, String> chat(@RequestBody String message) {
        Map<String, String> responses = new HashMap<>();

        String vertexAiGeminiResponse = vertexAiGeminiChatModel.call(message);
        responses.put("vertexai(gemini) 응답", vertexAiGeminiResponse);
        return responses;
    }
}