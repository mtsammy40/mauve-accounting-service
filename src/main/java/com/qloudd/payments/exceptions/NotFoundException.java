package com.qloudd.payments.exceptions;

public class NotFoundException extends Exception{
    private Class<?> tClass;
    private String identifier;

    public NotFoundException(Class<?> tClass, String identifier) {
        this.tClass = tClass;
        this.identifier = identifier;
    }

    @Override
    public String getMessage() {
        return tClass.getSimpleName() + " with identifier [ " + identifier + " ] does not exist";
    }
}
