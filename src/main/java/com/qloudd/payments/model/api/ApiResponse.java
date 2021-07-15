package com.qloudd.payments.model.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {
    private List<String> errors = new ArrayList<>();
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public ApiResponse<T> addError(String error) {
        this.errors.add(error);
        return this;
    }

    public ApiResponse<T> addErrors(List<String> errorList) {
        errorList = errorList != null ? errorList : Collections.emptyList();
        this.errors.addAll(errorList);
        return this;
    }
}
