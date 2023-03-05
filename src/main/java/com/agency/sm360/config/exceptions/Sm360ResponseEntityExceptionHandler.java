package com.agency.sm360.config.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class Sm360ResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Sm360Exception.class)
    protected ResponseEntity<Object> handleSm360Exception(Sm360Exception ex) {
        return buildResponseEntity(new AppError(ex));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleSm360Exception(Exception ex) {
        ex.printStackTrace();
        String errMsg = "An error has occurred, please contact your administrator.";
        Sm360Exception exp = new Sm360Exception(errMsg, ex.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(new AppError(exp));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return buildResponseEntity(new AppError(new Sm360Exception(ex.getMessage(), ex.getCause(), HttpStatus.BAD_REQUEST)));
    }

    private ResponseEntity<Object> buildResponseEntity(AppError error) {
        return new ResponseEntity<>(error, error.getStatus());
    }
}
