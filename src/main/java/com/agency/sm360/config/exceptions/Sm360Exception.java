package com.agency.sm360.config.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Sm360Exception extends Exception {
    private HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    private Object details;

    public Sm360Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Sm360Exception(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public Sm360Exception(String message) {
        super(message);
    }

    public Sm360Exception(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public Sm360Exception(Throwable cause) {
        super(cause);
    }

    public Sm360Exception(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }

}
