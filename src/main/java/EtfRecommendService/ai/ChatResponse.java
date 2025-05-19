package EtfRecommendService.ai;

public record ChatResponse(
        String assistantReply  // Gemini API가 생성한 답변
) {}