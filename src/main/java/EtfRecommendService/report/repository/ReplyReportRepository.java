package EtfRecommendService.report.repository;

import EtfRecommendService.report.domain.ReplyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
    long countByCommentId(Long aLong);
}
