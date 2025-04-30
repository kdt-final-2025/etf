package EtfRecommendService.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import EtfRecommendService.reply.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
