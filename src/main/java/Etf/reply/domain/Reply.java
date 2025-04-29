package Etf.reply.domain;

import Etf.user.User;
import jakarta.persistence.*;

@Entity
public class Reply {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
}
