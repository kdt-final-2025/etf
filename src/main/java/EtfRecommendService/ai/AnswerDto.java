package EtfRecommendService.ai;


/**
 * 사용자가 보낸 답변을 담는 record
 */
public record AnswerDto(
        int questionId,
        String answer
) {
}
