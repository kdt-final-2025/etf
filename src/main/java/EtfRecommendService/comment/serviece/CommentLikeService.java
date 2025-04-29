package EtfRecommendService.comment.serviece;


import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.comment.domain.CommentLike;
import EtfRecommendService.comment.repository.CommentLikeRepository;
import EtfRecommendService.comment.repository.CommentRepository;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {


    private final CommentLikeRepository commentLikeRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    //좋아요 토글
    @Transactional
    public void toggleLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean exists = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        if (exists) {
            // 이미 좋아요가 있으면 삭제
            commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        } else {
            // 좋아요 없으면 추가
            CommentLike like = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(like);
        }
    }


}
