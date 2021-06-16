package com.qloudd.payments.commons;

public enum Function {
    ACCOUNT_CREATION("Acc_Cr"),
    ACCOUNT_UPDATE("Acc_Up"),
    ACCOUNT_GET("Acc_Gt"),
    ACCOUNT_TRANSACTION("Acc_Tr"),
    ACCOUNT_TYPE_CREATION("AccTy_Cr"),
    ACCOUNT_TYPE_VALIDATION("AccTy_Vd");

    private final String code;

    private Function(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
}
