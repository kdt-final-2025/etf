package EtfRecommendService.reply;

public class TooFrequentCommentException extends RuntimeException {
    public TooFrequentCommentException(String message) {
        super(message);
    }
}
