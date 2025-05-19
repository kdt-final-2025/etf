package EtfRecommendService.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI로부터 받은 추천 결과를 담는 record
 */
public record Recommendation(
        String symbol,
        String name,
        @JsonProperty("reason") String reason
) {
}