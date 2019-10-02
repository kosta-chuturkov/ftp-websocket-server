package ftp.core.exception;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public class JsonException extends RuntimeException {

    private final String method;

    public JsonException(final String message, final String method) {
        super(message);
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }
}
