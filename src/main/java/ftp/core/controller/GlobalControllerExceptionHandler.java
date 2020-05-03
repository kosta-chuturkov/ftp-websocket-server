package ftp.core.controller;

import ftp.core.model.dto.ErrorDetails;
import ftp.core.model.dto.ErrorDetailsWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    private final HttpServletRequest request;

    @Autowired
    public GlobalControllerExceptionHandler(HttpServletRequest request) {
        this.request = request;
    }

    @ExceptionHandler({RestClientException.class})
    public ResponseEntity<Object> handleRestClientException(Exception e, HttpServletRequest request,
                                                            WebRequest webRequest) {
        LOG.error("Error when processing URL {}.",
                request.getRequestURL(), e);
        return processException(e, webRequest);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(Exception e, WebRequest request) {
        return processException(e, request);
    }


    private ResponseEntity<Object> processException(Exception e, WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return this.handleErrorInternal(e, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler({RuntimeException.class, IOException.class})
    public ResponseEntity<Object> handleInternalErrors(Exception e, WebRequest request) {
        return processException(e, request);
    }


    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e,
                                                                     WebRequest request) {
        return processException(e, request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error("Error when processing URL {}",
                ex.getMessage());
        return handleErrorInternal(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleExceptionWithCustomMessage(Exception ex, HttpHeaders headers,
                                                                    HttpStatus status, WebRequest request, String message) {
        String requestURI = this.request.getRequestURI();
        ErrorDetailsWrapper response = new ErrorDetailsWrapper();
        ErrorDetails error = new ErrorDetails();
        error.setCode(status.value());
        error.setMessage(message);
        error.setPath(requestURI);
        error.setTimestamp(LocalDateTime.now().toString());
        error.setType(ex.getClass().getSimpleName());
        response.addError(error);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return super.handleExceptionInternal(ex, response, headers, status, request);
    }

    private ResponseEntity<Object> handleErrorInternal(Exception ex, HttpHeaders headers,
                                                       HttpStatus status, WebRequest request) {
        String message = ex.getMessage();
        return handleExceptionWithCustomMessage(ex, headers, status, request, message);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        return this.handleErrorInternal(ex, headers, status, request);
    }

}





