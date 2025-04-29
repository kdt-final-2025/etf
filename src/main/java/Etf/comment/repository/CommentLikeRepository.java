package Etf.comment.repository;

import Etf.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    //좋아요 여부
    boolean existsByCommentIdAndUserId(String loginId, Long commentId);

    //좋아요 있음 삭제
    void deleteByCommentIdAndUserId(String loginId, Long commentId);
}
