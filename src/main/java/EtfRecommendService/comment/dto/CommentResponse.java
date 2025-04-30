package EtfRecommendService.comment.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long id,
        Long userId,
        String nickName,
        String content,
        Long likesCount,
        LocalDateTime createdAt
) {
}
