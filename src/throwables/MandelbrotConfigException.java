package throwables;
public class MandelbrotConfigException extends RuntimeException {
    public MandelbrotConfigException(String message) {
        super(message);
    }

    public MandelbrotConfigException(Throwable cause) {
        super(cause);
    }

    public MandelbrotConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
