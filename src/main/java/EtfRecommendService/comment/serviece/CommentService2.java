package EtfRecommendService.comment.serviece;

import EtfRecommendService.comment.domain.Comment;
import EtfRecommendService.comment.dto.CommentCreateRequest;
import EtfRecommendService.comment.dto.CommentRequest2;
import EtfRecommendService.comment.dto.CommentResponse2;
import EtfRecommendService.comment.dto.CommentUpdateRequest;
import EtfRecommendService.comment.exception.NoExistsEtfIdException;
import EtfRecommendService.comment.exception.NoExistsUserIdException;
import EtfRecommendService.comment.repository.CommentLikeRepository;
import EtfRecommendService.comment.repository.CommentRepository;
import EtfRecommendService.etf.Etf;
import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService2 {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;

    //Comment Create
    @Transactional
    public void create(CommentCreateRequest commentCreateRequest) {
        User user = userRepository.findById(commentCreateRequest.userId())
                .orElseThrow(() -> new NoExistsUserIdException("User ID not found"));
        Etf etf = etfRepository.findById(commentCreateRequest.etfId())
                .orElseThrow(() -> new NoExistsEtfIdException("Etf Id not found"));

        commentRepository.save(
                Comment.builder()
                        .content(commentCreateRequest.content())
                        .etf(etf)
                        .user(user)
                        .build()
        );
    }

    //Comment Update
    @Transactional
    public void update(Long commentId, Long userId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        comment.setContent(commentUpdateRequest.content());

        // 즉시 flush & audit 적용 확인
        commentRepository.flush();
        System.out.println("▶ updatedAt = " + comment.getUpdatedAt());
    }

    //Comment Soft Delete
    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        comment.setDeleted(true); // isDeleted = true 로 표시

        // sysout 으로 확인
        System.out.println(">> [Soft Delete] comment.id=" + comment.getId()
                + ", isDeleted=" + comment.isDeleted());


    }

    //댓글 조회
    @Transactional
    public CommentResponse2 getComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));


        boolean hasLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        long likeCount = commentLikeRepository.countByComment_Id(commentId);

        // (디버깅용)
        System.out.println("▶ 좋아요 개수 = " + likeCount);


        return CommentResponse2.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(comment.getUser().getUserName())
                .hasLiked(hasLiked)
                .likeCount(likeCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }


}
