package Etf.comment.serviece;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentRequest;
import Etf.comment.dto.CommentResponse;
import Etf.comment.exception.NoExistsEtfIdException;
import Etf.comment.exception.NoExistsUserIdException;
import Etf.comment.repository.CommentRepository;
import Etf.etf.Etf;
import Etf.etf.EtfRepository;
import Etf.user.User;
import Etf.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;

    public List<CommentResponse> readAll(Pageable pageable, Long etfId) {
        List<Comment> commentList = commentRepository.findByAllEtfId(etfId, pageable);
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
        return commentResponseList;
    }
}
