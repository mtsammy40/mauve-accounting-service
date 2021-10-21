package com.qloudd.payments.exceptions;

import com.qloudd.payments.enums.StatusCode;

public class PaymentExecutionException extends Exception {
    StatusCode statusCode;

    public PaymentExecutionException(String message, StatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public PaymentExecutionException(String message, Throwable cause, StatusCode statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
