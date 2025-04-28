package Etf.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record CommentResponse(
        Long id,
        Long etfId,
        Long userId,
//        String userName,
        String content,
        LocalDateTime createdAt
) {
}
