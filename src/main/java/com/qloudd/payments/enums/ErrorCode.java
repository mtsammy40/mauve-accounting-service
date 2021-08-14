package com.qloudd.payments.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // auth errors
    UNAUTHORIZED("A001", HttpStatus.UNAUTHORIZED, "Unauthorized"),
    // caused by client input
    VALIDATION_FAILED("V001", HttpStatus.BAD_REQUEST, "Details provided are invalid"),
    DUPLICATE_ACCOUNT_NUMBER("V0O2", HttpStatus.BAD_REQUEST, "Duplicate account number"),
    INVALID_ACCOUNTING_ENTRY("V003", HttpStatus.BAD_REQUEST, "Provided Accounting entry is invalid"),
    ACCOUNT_NOT_FOUND("V004", HttpStatus.NOT_FOUND, "Account not found"),
    NON_EMPTY_ACCOUNT("V005", HttpStatus.EXPECTATION_FAILED, "Account is not empty."),
    PRODUCT_NOT_FOUND("V006", HttpStatus.NOT_FOUND, "Product not found."),
    // 500-like errors (server faults and unexpected exceptions)
    UNEXPECTED_ERROR("E001", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
