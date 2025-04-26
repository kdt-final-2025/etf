package Etf.comment.serviece;


import Etf.comment.domain.Comment;
import Etf.comment.domain.CommentLike;
import Etf.comment.repository.CommentLikeRepository;
import Etf.comment.repository.CommentRepository;
import Etf.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {


    private final CommentLikeRepository commentLikeRepository;

    private final CommentRepository commentRepository;

    /** 좋아요 토글: 이미 눌렀으면 삭제, 안 눌렀으면 추가 */
    @Transactional
    public void toggleLike(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean alreadyLiked = commentLikeRepository.existsByCommentAndUser(comment, currentUser);
        if (alreadyLiked) {
            commentLikeRepository.deleteByCommentAndUser(comment, currentUser);
        } else {
            CommentLike like = CommentLike.builder()
                    .comment(comment)
                    .user(currentUser)
                    .build();
            commentLikeRepository.save(like);
        }
    }

    /** 현재 유저가 해당 댓글에 좋아요를 눌렀는지 */
    @Transactional
    public boolean hasLiked(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        return commentLikeRepository.existsByCommentAndUser(comment, currentUser);
    }

    /** 댓글별 총 좋아요 개수 조회 */
    @Transactional
    public long countLikes(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        return commentLikeRepository.countByComment(comment);
    }




}
