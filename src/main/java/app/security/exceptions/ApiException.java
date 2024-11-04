package app.security.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Purpose: To handle exceptions in the API
 * Author: Thomas Hartmann
 */
public class ApiException extends RuntimeException {

    private final int statusCode;
    @Getter
    private final String timestamp;

    public ApiException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now().toString();
    }

    public int getCode() {
        return statusCode;
    }

}
