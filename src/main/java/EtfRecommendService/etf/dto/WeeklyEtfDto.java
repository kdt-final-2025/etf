package EtfRecommendService.etf.dto;

import EtfRecommendService.etf.Theme;

import java.time.LocalDateTime;

public record WeeklyEtfDto(
        String etfName,
        String etfCode,
        Theme theme,
        double weeklyReturn
) {
}
