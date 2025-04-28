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

    /*    // 질문이 금융/주식 관련인지 확인
        if (!isFinanceOrStockQuestion(message)) {
            responses.put("error", "죄송합니다. 금융과 주식과 관련된 이야기만 할 수 있습니다.");
            return responses;
        }*/

        // AI에게 금융과 주식 관련 전문가로서 대답하도록 지시하는 프롬프트 생성
        String prompt = "당신은 주식 시장과 금융 분석에 전문 지식을 가진 금융 어드바이저입니다. "
                + "금융과 주식 외에 다른 질문에 대해서는 '죄송합니다. 금융과 주식과 관련된 이야기만 할 수 있습니다.' 라고 전달하세요. "
                + "질문: " + message;

        String vertexAiGeminiResponse = vertexAiGeminiChatModel.call(prompt);
        responses.put("vertexai(gemini) 응답", vertexAiGeminiResponse);
        return responses;
    }
}