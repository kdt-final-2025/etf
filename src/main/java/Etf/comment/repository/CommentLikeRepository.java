package Etf.comment.repository;

import Etf.comment.domain.Comment;
import Etf.comment.domain.CommentLike;
import Etf.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 1) 현재 유저가 이 댓글에 좋아요를 눌렀는지
    boolean existsByCommentAndUser(Comment comment, User user);

    // 2) 좋아요 취소 (댓글 + 유저 조합으로 삭제)
    void deleteByCommentAndUser(Comment comment, User user);

    // 3) 댓글별 좋아요 총 개수
    long countByComment(Comment comment);

}
