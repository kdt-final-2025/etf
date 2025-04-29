package Etf.comment.repository;

import Etf.comment.domain.Comment;
import Etf.etf.Etf;
import Etf.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findTopByUserOrderByCreatedAtDesc(User user);

    Optional<Comment> findTopByUserAndEtfOrderByCreatedAtDesc(User user, Etf etf);
    Page<Comment> findAllByEtfId(Long etfId, Pageable pageable);
}
