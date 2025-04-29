package EtfRecommendService.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public void createReport(@RequestBody ReportRequest rq){
    }
}
