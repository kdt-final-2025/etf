package Etf.comment.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse2(
        Long id,
        String content,
        String userName,
        boolean hasLiked,
        long likeCount,
        LocalDateTime createdAt
) {}