package ftp.core.exception;

import javax.servlet.ServletException;

public class ApiAuthenticationException extends ServletException {
    public ApiAuthenticationException(String message) {
        super(message);
    }
}
