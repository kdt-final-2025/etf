package EtfRecommendService.reply.repository.qdto;

import EtfRecommendService.reply.Reply;

public record ReplyAndLikesCountQDto(
        Reply reply,
        int likesCount
) {
}
