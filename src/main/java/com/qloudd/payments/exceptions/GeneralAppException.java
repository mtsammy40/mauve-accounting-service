package com.qloudd.payments.exceptions;

import com.qloudd.payments.enums.StatusCode;

import java.util.ArrayList;
import java.util.List;

public class GeneralAppException extends Exception {
    private StatusCode statusCode;
    private List<String> details = new ArrayList<>();

    public GeneralAppException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }

    public GeneralAppException(StatusCode statusCode, List<String> details) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.details = details;
    }

    public StatusCode getErrorCode() {
        return statusCode;
    }

    public void setErrorCode(StatusCode statusCode) {
        this.statusCode = statusCode;
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
