public class ConfigDataException extends RuntimeException {
    public ConfigDataException(String message) {
        super(message);
    }

    public ConfigDataException(Throwable cause) {
        super(cause);
    }

    public ConfigDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
