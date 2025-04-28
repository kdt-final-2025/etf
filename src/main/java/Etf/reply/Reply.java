package Etf.reply;

import Etf.comment.Comment;
import Etf.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = false)
@ToString
public class Reply {
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
    @ManyToOne
    private CommentLike commentLike;
}
