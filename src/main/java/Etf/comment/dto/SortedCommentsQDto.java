package Etf.comment.dto;

import Etf.comment.domain.Comment;
import lombok.Builder;

import java.util.List;
@Builder
public record SortedCommentsQDto(
        List<Comment> commentList,
        List<Long> likesCountList,
        Long totalCount
) {
}
