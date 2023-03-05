package com.agency.sm360.config.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppError {
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
    private Object details;

    public AppError(Sm360Exception ex) {
        this.timestamp = LocalDateTime.now();
        this.message = ex.getMessage();
        this.status = ex.getStatus();
    }

}
