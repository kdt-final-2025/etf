package Etf.user.dto;

import Etf.comment.Comment;
import Etf.etf.Etf;

import java.util.List;

public record UserPageResponse(Long id,
                               String loginId,
                               String nickName,
                               String imageUrl,
                               Boolean isLikePrivate,
                               List<Comment> comments,
                               List<Etf> etfs
                             ) {
}
