package EtfRecommendService.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportService {
    private final CommentReportRepository commentReportRepository;
    private final ReplyReportRepository replyReportRepository;

    public void create(String loginId, ReportRequest rq) {

    }
}
