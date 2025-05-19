package EtfRecommendService.aiChat;

public record ChatRequest(
        String userInput,      // 사용자가 보낸 메시지
        String history         // (선택) 이전 대화 이력. 없으면 빈 문자열("")
) {}