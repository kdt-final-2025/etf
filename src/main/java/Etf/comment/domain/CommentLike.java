package Etf.comment.domain;

import Etf.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Getter
public class CommentLike{

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
    @ManyToOne
    private Comment comment;
}
