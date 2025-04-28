package Etf.reply.repository;

import Etf.reply.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findFirstByUserIdAndCommentIdOrderByCreatedAtDesc(Long userId, Long commentId);
}
