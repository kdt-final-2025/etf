package EtfRecommendService.etf.dto;

public record EtfReadResponse(
        Long etfId,
        String etfName,
        String etfCode
) {
}
