package EtfRecommendService.user.dto;

import EtfRecommendService.comment.Comment;
import EtfRecommendService.etf.domain.Etf;

import java.util.List;

public record UserPageResponse(Long id,
                               String loginId,
                               String nickName,
                               String imageUrl,
                               Boolean isLikePrivate
                             ) {
}
