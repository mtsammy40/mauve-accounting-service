package com.qloudd.payments.integration.mauve.mpesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MpesaResponseWrapper<T> {
    Boolean isSuccessful;
    T responseBody;
}
