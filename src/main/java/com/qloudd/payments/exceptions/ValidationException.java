package com.qloudd.payments.exceptions;

import java.util.List;

public class ValidationException extends Exception {
    private List<String> errorList;

    public ValidationException(List<String> errorList) {
        this.errorList = errorList;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
