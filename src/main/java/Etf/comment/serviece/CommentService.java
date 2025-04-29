package Etf.comment.serviece;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentResponse;
import Etf.comment.dto.CommentsPageList;
import Etf.comment.repository.qdto.CommentAndLikesCountQDto;
import Etf.comment.repository.qdto.SortedCommentsQDto;
import Etf.comment.repository.CommentRepository;
import Etf.comment.repository.CommentRepositoryCustom;
import Etf.user.User;
import Etf.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRepositoryCustom commentRepositoryCustom;
    private final UserRepository userRepository;

    public CommentsPageList readAll(String loginId, Pageable pageable, Long etfId) {
        Sort sort = pageable.getSort();

        if(sort.toString().equals("likes")){
            SortedCommentsQDto qDto = commentRepositoryCustom.findAllByEtfIdOrderByLikes(pageable, etfId);
            List<CommentAndLikesCountQDto> commentAndLikesCountQDtoPage = qDto.commentAndLikesCountQDtoPage();

            List<CommentResponse> commentResponseList = commentAndLikesCountQDtoPage.stream()
                    .map(c->{
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
                    .totalPages((int)Math.ceil((double) qDto.totalCount()/pageable.getPageSize()))
                    .etfId(etfId)
                    .commentResponses(commentResponseList)
                    .build();
        }
        else {
            Page<Comment> commentPage = commentRepository.findAllByEtfId(etfId, pageable);
            List<Comment> commentList = commentPage.getContent();
            List<CommentResponse> commentResponseList = commentList.stream().map(
                            c-> CommentResponse
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
                    .totalPages((int)Math.ceil((double) commentPage.getTotalElements()/pageable.getPageSize()))
                    .etfId(etfId)
                    .commentResponses(commentResponseList)
                    .build();
        }
    }
}
