package EtfRecommendService.report.service;

import EtfRecommendService.comment.Comment;
import EtfRecommendService.comment.CommentRepository;
import EtfRecommendService.notification.NotificationService;
import EtfRecommendService.reply.domain.Reply;
import EtfRecommendService.reply.repository.ReplyRepository;
import EtfRecommendService.report.domain.ReportType;
import EtfRecommendService.report.domain.CommentReport;
import EtfRecommendService.report.domain.ReplyReport;
import EtfRecommendService.report.domain.ReportReason;
import EtfRecommendService.report.dto.ReportRequest;
import EtfRecommendService.report.repository.CommentReportRepository;
import EtfRecommendService.report.repository.ReplyReportRepository;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportService {
    private final CommentReportRepository commentReportRepository;
    private final ReplyReportRepository replyReportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final NotificationService notificationService;

    @Transactional
    public void create(String loginId, ReportRequest rq) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        if (rq.commentId() != null){
            Comment comment = commentRepository.findById(rq.commentId())
                    .orElseThrow(()->new IllegalArgumentException("Comment Not found"));

            CommentReport report = CommentReport.builder()
                    .comment(comment)
                    .reporter(user)
                    .reportReason(ReportReason.toEnum(rq.reportReason()))
                    .build();
            report.addReport(comment, user);

            commentReportRepository.save(report);

            long reportedCount = commentReportRepository.countByCommentId(rq.commentId());
            if (reportedCount >= 10){
                notificationService.notifyIfReportedOverLimit(rq.commentId(), ReportType.COMMENT);
            }
        }
        else {
            Reply reply = replyRepository.findById(rq.replyId())
                    .orElseThrow(()->new IllegalArgumentException("Reply not found"));

            ReplyReport report = ReplyReport.builder()
                    .reporter(user)
                    .reply(reply)
                    .reportReason(ReportReason.toEnum(rq.reportReason()))
                    .build();

            report.addReport(reply, user);
            replyReportRepository.save(report);

            long reportedCount = replyReportRepository.countByReplyId(rq.replyId());
            if (reportedCount >= 10){
                notificationService.notifyIfReportedOverLimit(rq.replyId(), ReportType.REPLY);
            }
        }
    }
}
