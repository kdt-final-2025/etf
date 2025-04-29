package Etf.comment.repository.qdto;

import Etf.comment.domain.Comment;

public record CommentAndLikesCountQDto(
        Comment comment,
        Long likesCount
) {
}
