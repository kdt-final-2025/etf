package Etf.comment.serviece;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentResponse;
import Etf.comment.dto.SortedCommentsQDto;
import Etf.comment.repository.CommentRepository;
import Etf.comment.repository.CommentRepositoryCustom;
import Etf.etf.EtfRepository;
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

    public Page<CommentResponse> readAll(Pageable pageable, Long etfId) {
        Sort sort = pageable.getSort();

        if(sort.toString().equals("likes")){
            SortedCommentsQDto qDto = commentRepositoryCustom.findAllByEtfIdOrderByLikes(pageable, etfId);
            List<Comment> commentList = qDto.commentList();
            Long totalCount = qDto.totalCount();
            List<Long> likesCountList = qDto.likesCountList();

            List<CommentResponse> commentResponseList = new ArrayList<>();
            for (int i = 0; i < commentList.size(); i++) {
                commentResponseList.add(
                        CommentResponse.builder()
                                .content(commentList.get(i).getContent())
                                .likesCount(likesCountList.get(i))
                                .userName(commentList.get(i).getUser().getNickName())
                                .etfId(commentList.get(i).getEtf().getId())
                                .createdAt(commentList.get(i).getCreatedAt())
                                .id(commentList.get(i).getId())
                                .userId(commentList.get(i).getUser().getId())
                                .build()
                );
            }

            return new PageImpl<>(commentResponseList,pageable,totalCount);
        }
        else {
            Page<Comment> commentPage = commentRepository.findAllByEtfId(etfId, pageable);
            List<Comment> commentList = commentPage.getContent();
            List<CommentResponse> commentResponseList = commentList.stream().map(
                            c-> CommentResponse
                                    .builder()
                                    .id(c.getId())
                                    .etfId(c.getEtf().getId())
                                    .userId(c.getUser().getId())
                                    .content(c.getContent())
                                    .createdAt(c.getCreatedAt())
                                    .build()
                    )
                    .toList();
            return new PageImpl<>(commentResponseList, pageable, commentPage.getTotalElements());
        }
    }
}
