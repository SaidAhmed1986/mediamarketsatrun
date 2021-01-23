package org.mediamarktsaturn.order.order.exception;


import lombok.Getter;

public class OrderManagerException extends RuntimeException {
    @Getter
    private int statusCode;

    public OrderManagerException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public OrderManagerException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public OrderManagerException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }
}
