package Etf.comment.domain;

import Etf.etf.Etf;
import Etf.user.User;
import Etf.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "comment")
    @Builder.Default
    private List<CommentLike> commentLikes = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private int reportCount = 0;

    @ManyToOne
    @ToString.Exclude
    private Etf etf;

    @ManyToOne
    @ToString.Exclude
    private User user;
    @OneToMany
    @ToString.Exclude
    @Builder.Default
    private List<CommentLike> commentLikeList = new ArrayList<>();

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
