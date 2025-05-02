package EtfRecommendService.etf.dto;

import EtfRecommendService.etf.Theme;

public record MonthlyEtfDto(
        String etfName,
        String etfCode,
        Theme theme,
        double monthlyReturn
) {
}
