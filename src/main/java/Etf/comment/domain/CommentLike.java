package Etf.comment.domain;

import Etf.user.User;
import Etf.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "comment_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_user",
                columnNames = {"comment_id", "user_id"}
        )
)
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@AllArgsConstructor
public class CommentLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private Comment comment;

    @ManyToOne
    @ToString.Exclude
    private User user;


}
