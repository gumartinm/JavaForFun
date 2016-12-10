package de.spring.webservices.rest.controller.handler;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// using XML configuration @EnableWebMvc
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // 500
    @ExceptionHandler({ IOException.class })
    public ResponseEntity<Object> handleBadRequest(final Exception ex, final WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
