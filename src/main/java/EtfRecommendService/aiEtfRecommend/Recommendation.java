package EtfRecommendService.aiEtfRecommend;

public record Recommendation(
        String symbol,
        String name,
        String reason
) {}