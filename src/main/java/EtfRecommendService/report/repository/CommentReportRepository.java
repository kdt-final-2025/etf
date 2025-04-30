package EtfRecommendService.report.repository;

import EtfRecommendService.report.domain.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    long countByCommentId(Long aLong);
}
