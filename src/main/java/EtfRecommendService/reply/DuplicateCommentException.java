package EtfRecommendService.reply;

public class DuplicateCommentException extends RuntimeException {
    public DuplicateCommentException(String message) {
        super(message);
    }
}
