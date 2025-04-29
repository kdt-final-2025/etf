package Etf.comment.repository;

import Etf.comment.domain.Comment;
import EtfRecommendService.etf.Etf;
import EtfRecommendService.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findTopByUserOrderByCreatedAtDesc(User user);

    Optional<Comment> findTopByUserAndEtfOrderByCreatedAtDesc(User user, Etf etf);
}
