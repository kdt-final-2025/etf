package EtfRecommendService.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long etfId,
        Long userId,
//        String userName,
        String content,
        LocalDateTime createdAt
) {
}
