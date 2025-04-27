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

    //좋아요 토글
    @Transactional
    public void toggleLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        boolean already = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
        if (already) {
            commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
        } else {
            // comment와 userId로 CommentLike 빌드
            CommentLike like = CommentLike.builder()
                    .comment(comment)
                    .user(User.builder().id(userId).build())  // User 객체를 id만 들고 임시 생성
                    .build();
            commentLikeRepository.save(like);
        }
    }


}
