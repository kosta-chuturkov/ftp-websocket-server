package ftp.core.exception;

public class FileAccessDeniedException extends RuntimeException {
    public FileAccessDeniedException() {
    }

    public FileAccessDeniedException(String message) {
        super(message);
    }

    public FileAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public FileAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
