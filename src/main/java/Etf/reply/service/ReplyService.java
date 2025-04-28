package Etf.reply.service;

import Etf.comment.Comment;
import Etf.comment.CommentRepository;
import Etf.comment.NotFoundCommentIdException;
import Etf.reply.DuplicateCommentException;
import Etf.reply.Reply;
import Etf.reply.TooFrequentCommentException;
import Etf.reply.dto.ReplyRequest;
import Etf.reply.repository.ReplyRepository;
import Etf.user.User;
import Etf.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final Clock clock;

    @Transactional
    public void create(String loginId, @Valid ReplyRequest rq) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new IllegalArgumentException("Not found Login Id"));
        Comment comment = commentRepository.findById(rq.commentId()).orElseThrow(() -> new NotFoundCommentIdException("Not found Comment Id"));

        //유저가 해당 댓글에 작성한 가장 최근 대댓글 조회( 작성한 대댓글이 없을 시 Null 반환 )
        Optional<Reply> recentReply = replyRepository.findFirstByUserIdAndCommentIdOrderByCreatedAtDesc(user.getId(), comment.getId());

        if (recentReply.isPresent()){
            //해당 댓글의 가장 최근 본인이 작성한 대댓글과 같은 내용 작성시 예외 발생
            if (recentReply.equals(rq.content())){
                throw new DuplicateCommentException("Duplicate comment detected");
            }

            Instant instantFromCreatedAtTime = recentReply.get().getCreatedAt().atZone(ZoneId.of("UTC")).toInstant();
            Instant instantFromClock = clock.instant();
            Duration duration = Duration.between(instantFromCreatedAtTime, instantFromClock);
            //5초 이하의 간격으로 대댓글 작성시 예외 발생
            if (Math.abs(duration.getSeconds())<=5){
                throw new TooFrequentCommentException("Too Frequent Comment");
            }
        }

        Reply reply = Reply.builder()
                        .content(rq.content())
                        .user(user)
                        .comment(comment)
                        .build();

        reply.addCommentAndUser(comment, user);
        replyRepository.save(reply);
    }
}
