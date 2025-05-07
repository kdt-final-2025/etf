package EtfRecommendService.user.dto;


import java.time.LocalDateTime;

public record UserCommentResponse(
        Long commentId,
        Long etfId,
        Long userId,
        String userName,
        String content,
        String ProfileImgUrl,
        LocalDateTime createdAt) {
}
