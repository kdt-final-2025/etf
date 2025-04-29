package Etf.comment.repository.qdto;

import Etf.comment.domain.Comment;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
@Builder
public record SortedCommentsQDto(
        List<CommentAndLikesCountQDto> commentAndLikesCountQDtoPage,
        Long totalCount
) {
}
