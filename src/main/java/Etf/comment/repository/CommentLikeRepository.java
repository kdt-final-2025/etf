package Etf.comment.repository;

import Etf.comment.domain.Comment;
import Etf.comment.domain.CommentLike;
import Etf.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    //좋아요 총 갯수
    long countByComment_Id(Long commentId);

    //좋아요 여부
    boolean existsByComment_IdAndUser_Id(Long commentId, Long userId);

    //좋아요 취소
    void deleteByComment_IdAndUser_Id(Long commentId, Long userId);
}
