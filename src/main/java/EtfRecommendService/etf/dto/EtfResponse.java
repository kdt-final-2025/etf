package EtfRecommendService.etf.dto;

import java.util.List;

public record EtfResponse<T>(
        int totalPage,
        Long totalCount,
        int currentPage,
        int pageSize,
        List<T> etfReadResponseList
) {
}
