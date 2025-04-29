package EtfRecommendService.comment;

import EtfRecommendService.reply.domain.Reply;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "comment")
    private List<Reply> replyList = new ArrayList<>();
}
