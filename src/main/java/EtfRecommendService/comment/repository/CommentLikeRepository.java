package EtfRecommendService.comment.repository;

import EtfRecommendService.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    //좋아요 총 갯수
    long countByComment_Id(Long commentId);

    //좋아요 여부
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    //좋아요 있음 삭제
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
