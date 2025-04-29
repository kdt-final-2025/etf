package EtfRecommendService.reply;

public class NotFoundUserLoginIdException extends RuntimeException {
    public NotFoundUserLoginIdException(String message) {
        super(message);
    }
}
