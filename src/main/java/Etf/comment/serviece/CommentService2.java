package Etf.comment.serviece;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentCreateRequest;
import Etf.comment.dto.CommentResponse2;
import Etf.comment.dto.CommentUpdateRequest;
import Etf.comment.repository.CommentLikeRepository;
import Etf.comment.repository.CommentRepository;
import Etf.etf.EtfRepository;
import Etf.user.User;
import Etf.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

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


        // 1) 같은 ETF에 동일한 내용의 마지막 댓글이 있다면 차단
        commentRepository
                .findTopByUserAndEtfOrderByCreatedAtDesc(user, etf)
                .ifPresent(last -> {
                    if (last.getContent().equals(commentCreateRequest.content())) {
                        throw new IllegalArgumentException("똑같은 댓글은 다시 작성할 수 없습니다.");
                    }
                });

        // 2) 어떤 ETF든 사용자가 마지막으로 작성한 댓글과의 시간 차가 5초 미만이면 차단
        commentRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .ifPresent(last -> {
                    Duration diff = Duration.between(last.getCreatedAt(), LocalDateTime.now());
                    if (diff.getSeconds() < 5) {
                        throw new IllegalArgumentException("한 번 작성 후 최소 5초 뒤에 다시 작성 가능합니다.");
                    }
                });


// 조건 통과 시 저장
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
