package Etf.comment.serviece;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentCreateRequest;
import Etf.comment.dto.CommentResponse;
import Etf.comment.dto.CommentUpdateRequest;
import Etf.comment.dto.CommentsPageList;
import Etf.comment.exception.NoExistsEtfIdException;
import Etf.comment.exception.NoExistsUserIdException;
import Etf.comment.repository.qdto.CommentAndLikesCountQDto;
import Etf.comment.repository.qdto.SortedCommentsQDto;
import Etf.comment.repository.CommentRepository;
import Etf.comment.repository.CommentRepositoryCustom;
import Etf.etf.Etf;
import Etf.etf.EtfRepository;
import Etf.user.User;
import Etf.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRepositoryCustom commentRepositoryCustom;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;

    public CommentsPageList readAll(Pageable pageable, Long etfId) {
        Sort sort = pageable.getSort();
        String sortOrderName = sort.get().findFirst().map(Sort.Order::getProperty).orElse("createdAt");

        if (sortOrderName.equals("likes")) {
            SortedCommentsQDto qDto = commentRepositoryCustom.findAllByEtfIdOrderByLikes(pageable, etfId);
            List<CommentAndLikesCountQDto> commentAndLikesCountQDtoPage = qDto.commentAndLikesCountQDtoPage();

            List<CommentResponse> commentResponseList = commentAndLikesCountQDtoPage.stream()
                    .map(c -> {
                        return CommentResponse.builder()
                                .id(c.comment().getId())
                                .userId(c.comment().getUser().getId())
                                .nickName(c.comment().getUser().getNickName())
                                .content(c.comment().getContent())
                                .likesCount(c.likesCount())
                                .createdAt(c.comment().getCreatedAt())
                                .build();
                    }).toList();

            return CommentsPageList.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(qDto.totalCount())
                    .totalPages((int) Math.ceil((double) qDto.totalCount() / pageable.getPageSize()))
                    .etfId(etfId)
                    .commentResponses(commentResponseList)
                    .build();
        } else {
            Page<Comment> commentPage = commentRepository.findAllByEtfId(etfId, pageable);
            List<Comment> commentList = commentPage.getContent();
            List<CommentResponse> commentResponseList = commentList.stream().map(
                            c -> CommentResponse
                                    .builder()
                                    .id(c.getId())
                                    .userId(c.getUser().getId())
                                    .content(c.getContent())
                                    .createdAt(c.getCreatedAt())
                                    .build()
                    )
                    .toList();
            return CommentsPageList.builder()
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalElements(commentPage.getTotalElements())
                    .totalPages((int) Math.ceil((double) commentPage.getTotalElements() / pageable.getPageSize()))
                    .etfId(etfId)
                    .commentResponses(commentResponseList)
                    .build();
        }
    }

    //Comment Create
    @Transactional
    public void create(String loginId, CommentCreateRequest commentCreateRequest) {
        User user = userRepository.findByLoginId(loginId)
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
    public void update(String loginId, Long commentId, CommentUpdateRequest commentUpdateRequest) {
        // 1) 로그인된 유저 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("User ID not found"));

        // 2) 수정 대상 댓글 로드
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment ID not found"));

        // 3) 권한 검사: 작성자와 일치해야
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        comment.setContent(commentUpdateRequest.content());

        commentRepository.flush();
        System.out.println("▶ updatedAt = " + comment.getUpdatedAt());
    }

//    public void update(String loginId, Long commentId, CommentUpdateRequest commentUpdateRequest) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
//        if (!comment.getUser().getId().equals(loginId)) {
//            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
//        }
//        comment.setContent(commentUpdateRequest.content());
//
//        // 즉시 flush & audit 적용 확인
//        commentRepository.flush();
//        System.out.println("▶ updatedAt = " + comment.getUpdatedAt());
//    }

    //Comment Soft Delete
    @Transactional
    public void delete(String loginId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        comment.setDeleted(true); // isDeleted = true 로 표시

        // sysout 으로 확인
        System.out.println(">> [Soft Delete] comment.id=" + comment.getId()
                + ", isDeleted=" + comment.isDeleted());


    }
}
