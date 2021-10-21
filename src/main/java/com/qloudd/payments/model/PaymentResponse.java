package com.qloudd.payments.model;

import com.qloudd.payments.enums.StatusCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse<T> {
    StatusCode statusCode;
    T data;
}
