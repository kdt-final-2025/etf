package EtfRecommendService.report.controller;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.report.dto.ReportRequest;
import EtfRecommendService.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("user/reports")
    public ResponseEntity<String> createReport(@LoginMember String loginId, @RequestBody ReportRequest rq){
        reportService.create(loginId, rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply was Reported");
    }
}
