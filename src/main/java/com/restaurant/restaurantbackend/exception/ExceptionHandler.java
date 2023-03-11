package com.restaurant.restaurantbackend.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<Object> handleConflict(CustomException exception, WebRequest request) {
        String responseBody = exception.getMessage();
        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), exception.getHttpStatus(), request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = DataAccessException.class)
    protected ResponseEntity<Object> handleConflict(DataAccessException exception, WebRequest request) {
        return handleConflict(new CustomException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR), request);
    }
}
