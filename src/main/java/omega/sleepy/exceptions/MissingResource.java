package omega.sleepy.exceptions;

public class MissingResource extends RuntimeException {
    public MissingResource(String message) {
        super(message);
    }
}
