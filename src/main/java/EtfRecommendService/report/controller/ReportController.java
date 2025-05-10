package EtfRecommendService.report.controller;

import EtfRecommendService.loginUtils.LoginMember;
import EtfRecommendService.report.dto.ReportListResponse;
import EtfRecommendService.report.dto.ReportRequest;
import EtfRecommendService.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    @Secured("USER")
    @PostMapping
    public ResponseEntity<String> createReport(@LoginMember String loginId, @RequestBody ReportRequest rq){
        reportService.create(loginId, rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply was Reported");
    }

    @Secured("ADMIN")
    //전체 신고 목록 조회
    @GetMapping
    public ResponseEntity<ReportListResponse> readAllReports(@LoginMember String loginId) {
        return ResponseEntity.ok(reportService.readAll(loginId));
    }
}
