package EtfRecommendService.report;

import EtfRecommendService.reply.domain.Reply;
import EtfRecommendService.user.User;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
public class ReplyReport extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long Id;

    @ManyToOne
    @ToString.Exclude
    private Reply reply;

    @ManyToOne
    @ToString.Exclude
    private User reporter;

    @Column(nullable = false)
    private ReportReason reportReason;
}
