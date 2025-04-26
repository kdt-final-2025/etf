package Etf.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest2(
        @NotNull
        Long etfId,
        @NotNull@Size(max = 1000, min = 2)
        String content,
        @NotNull
        Long parentId
) {
}
