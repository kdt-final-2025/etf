package EtfRecommendService.reply.service;

import EtfRecommendService.comment.Comment;
import EtfRecommendService.comment.CommentRepository;
import EtfRecommendService.comment.NotFoundCommentIdException;
import EtfRecommendService.reply.*;
import EtfRecommendService.reply.dto.RepliesPageList;
import EtfRecommendService.reply.dto.ReplyRequest;
import EtfRecommendService.reply.dto.ReplyResponse;
import EtfRecommendService.reply.repository.ReplyRepository;
import EtfRecommendService.reply.repository.ReplyRepositoryCustom;
import EtfRecommendService.reply.repository.qdto.ReplyAndLikesCountQDto;
import EtfRecommendService.reply.repository.qdto.SortedRepliesQDto;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final Clock clock;
    private final ReplyRepositoryCustom replyRepositoryCustom;

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

            Instant instantFromCreatedAtTime = recentReply.get().getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toInstant();
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

    public RepliesPageList readAll(String loginId, Long commentId, Pageable pageable) {
        Sort sort = pageable.getSort();
        String sortOrderName = sort.stream().findFirst().map(Sort.Order::getProperty).orElse("createdAt");

        List<ReplyResponse> replyResponseList;

        if (sortOrderName.equals("likes")){
            SortedRepliesQDto sortedRepliesQDto = replyRepositoryCustom.findAllByCommentIdOrderByLikes(pageable, commentId);
            List<ReplyAndLikesCountQDto> replyAndLikesCountQDtoList = sortedRepliesQDto.replyAndLikesCountQDtoList();

            replyResponseList = replyAndLikesCountQDtoList.stream()
                    .map(r->ReplyResponse.builder()
                                .id(r.reply().getId())
                                .userId(r.reply().getUser().getId())
                                .nickName(r.reply().getUser().getNickName())
                                .content(r.reply().getContent())
                                .likesCount(r.likesCount())
                                .createdAt(r.reply().getCreatedAt())
                                .build())
                    .toList();

            return RepliesPageList.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(sortedRepliesQDto.totalElements())
                    .totalPages((int)Math.ceil((double) sortedRepliesQDto.totalElements()/pageable.getPageSize()))
                    .commentId(commentId)
                    .replyResponses(replyResponseList)
                    .build();
        }
        else {
            Page<Reply> replyPage = replyRepository.findAllByCommentId(commentId, pageable);
            List<Reply> replyList = replyPage.getContent();
            replyResponseList = replyList.stream().map(
                            r-> ReplyResponse
                                    .builder()
                                    .id(r.getId())
                                    .userId(r.getUser().getId())
                                    .content(r.getContent())
                                    .createdAt(r.getCreatedAt())
                                    .build()
                    )
                    .toList();

            return RepliesPageList.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(replyPage.getTotalElements())
                    .totalPages((int)Math.ceil((double) replyPage.getTotalElements()/pageable.getPageSize()))
                    .commentId(commentId)
                    .replyResponses(replyResponseList)
                    .build();
        }
    }

    @Transactional
    public void update(String loginId, Long replyId, ReplyRequest rq) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(()->new NotFoundUserLoginIdException("Not found User"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(()->new NotFoundReplyIdException("Not found Reply Id"));

        if (reply.getUser().equals(user)){
            reply.update(rq.commentId(), rq.content());
        }
        else throw new IllegalArgumentException("Permission denied to edit this comment.");
    }
}
