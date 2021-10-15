package com.qloudd.payments.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomLogger {
    Logger LOG;

    private String uniqueIdentifier;
    private Function functionCode;
    
    public CustomLogger(Class<?> clazz) {
        LOG = LoggerFactory.getLogger(clazz);
    }

    public void info(String log, Object... objects) {
        LOG.info("{} | {} | " + log + " | {}", functionCode.getCode(), uniqueIdentifier, objects);
    }

    public void debug(String log, Object... objects) {
        LOG.debug("{} | {} | " + log, functionCode.getCode(), uniqueIdentifier, objects);
    }

    public void warn(String log, Object... objects) {
        LOG.warn("{} | {} | " + log, functionCode.getCode(), uniqueIdentifier, objects);
    }

    public void error(String log, Object... objects) {
        LOG.error("{} | {} | " + log, functionCode.getCode(), uniqueIdentifier, objects);
    }

    public void setfunctionCode(Function functionCode) {
        this.functionCode = functionCode;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public void update(Function functionCode, String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.functionCode = functionCode;
    }

    public Function getfunctionCode() {
        return functionCode;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }
}
