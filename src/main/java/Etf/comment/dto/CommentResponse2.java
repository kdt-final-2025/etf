package Etf.comment.dto;

import java.time.LocalDateTime;


public record CommentResponse2(
        Long id,
        String content,
        String user,
        boolean hasLiked,
        long likeCount,
        LocalDateTime createdAt
) {}