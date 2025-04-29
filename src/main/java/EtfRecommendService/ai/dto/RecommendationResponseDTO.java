package EtfRecommendService.ai.dto;

public record RecommendationResponseDTO(
        String status,
        RecommendationDTO recommendation
) {
}
