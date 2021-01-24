package org.mediamarktsaturn.order.common.exception;


import lombok.Getter;

public class ProcessingOrderException extends RuntimeException {
    @Getter
    private int statusCode;

    public ProcessingOrderException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ProcessingOrderException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public ProcessingOrderException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }
}
