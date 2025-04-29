package EtfRecommendService.reply.domain;

import EtfRecommendService.comment.Comment;
import EtfRecommendService.user.User;
import EtfRecommendService.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = false)
@ToString
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne
    private Comment comment;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "reply")
    @Builder.Default
    private List<ReplyLike> replyLikeList = new ArrayList<>();

    public void addCommentAndUser(Comment comment, User user){
        this.comment = comment;
        this.user = user;
        comment.getReplyList().add(this);
        //User 엔티티에 관계 필드 생성될때까지 주석처리
//        user.getReplyList().add(this);
    }

    public void update(Long commentId, String content) {

    }
}
