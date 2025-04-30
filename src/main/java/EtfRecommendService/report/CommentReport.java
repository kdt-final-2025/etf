package EtfRecommendService.report;

import EtfRecommendService.comment.Comment;
import EtfRecommendService.reply.domain.Reply;
import EtfRecommendService.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
public class CommentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long Id;

    @ManyToOne
    @ToString.Exclude
    private Comment comment;

    @ManyToOne
    @ToString.Exclude
    private User reporter;

    @Column(nullable = false)
    private ReportReason reportReason;
}
