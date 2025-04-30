package EtfRecommendService.report;

import lombok.Builder;

@Builder
public record ReportRequest(
        Long commentId,
        Long replyId,
        String reportReason
) {
}
