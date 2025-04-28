package Etf.reply.dto;

public record ReplyResponse(
        Long id,
        Long userId,
        Long commentId,
        String content,
        Long likesCount
) {
}
