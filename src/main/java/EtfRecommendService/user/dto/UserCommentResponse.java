package EtfRecommendService.user.dto;


import java.time.LocalDateTime;

// comment.id,
//                        comment.etf.id,
//                        comment.user.id,
//                        comment.user.nickName,
//                        comment.content,
//                        comment.user.imageUrl,
//                        comment.createdAt
public record UserCommentResponse(
        Long commentId,
        Long etfId,
        Long userId,
        String userName,
        String content,
        String ProfileImgUrl,
        LocalDateTime createdAt) {
}
