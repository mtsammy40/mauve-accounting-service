package com.qloudd.payments.exceptions;

import com.qloudd.payments.enums.ErrorCode;

import java.util.List;

public class GeneralAppException extends Exception {
    private ErrorCode errorCode;
    private List<String> details;

    public GeneralAppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GeneralAppException(ErrorCode errorCode, List<String> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void addDetail(String detail) {
        this.details.add(detail);
    }

}
