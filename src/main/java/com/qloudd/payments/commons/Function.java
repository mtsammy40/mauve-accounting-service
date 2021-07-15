package com.qloudd.payments.commons;

public enum Function {
    ACCOUNT_CREATION("Acc_Cr"),
    ACCOUNT_UPDATE("Acc_Up"),
    ACCOUNT_GET("Acc_Gt"),
    ACCOUNT_TRANSACTION("Acc_Tr"),
    ACCOUNT_TYPE_CREATION("AccTy_Cr"),
    ACCOUNT_TYPE_VALIDATION("AccTy_Vd"),
    PRODUCT_VALIDATION("Pr_Vd"),
    PRODUCT_CREATION("Pr_Cr"),
    PRODUCT_GET_ONE("Pr_Get"),
    PRODUCT_LIST("Pr_Li"),
    PRODUCT_UPDATE("Pr_Up"),
    TRANSACTION_TRANSFER("Tr_Tr");

    private final String code;

    private Function(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
}
