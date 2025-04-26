package Etf.comment.serviece;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentRequest2;
import Etf.comment.exception.NoExistsEtfIdException;
import Etf.comment.exception.NoExistsUserIdException;
import Etf.comment.repository.CommentRepository;
import Etf.etf.Etf;
import Etf.etf.EtfRepository;
import Etf.user.User;
import Etf.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService2 {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;

    //Comment Create
    @Transactional
    public void create(CommentRequest2 commentRequest2) {
        User user = userRepository.findById(commentRequest2.userId())
                .orElseThrow(() -> new NoExistsUserIdException("User ID not found"));
        Etf etf = etfRepository.findById(commentRequest2.etfId())
                .orElseThrow(() -> new NoExistsEtfIdException("Etf Id not found"));

        commentRepository.save(
                Comment.builder()
                        .content(commentRequest2.content())
                        .etf(etf)
                        .user(user)
                        .build()
        );
    }

    //Comment Update
    @Transactional
    public void update(Long commentId, CommentRequest2 commentRequest2) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        // 직접 setter 호출
        comment.setContent(commentRequest2.content());
    }

    //Comment Soft Delete
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        comment.isDeleted();  // isDeleted = true 로 표시
    }
}
