package com.qloudd.payments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class TransactionMisc {
    String description;
    List<RequestLog> requestLogs;

    public List<RequestLog> getRequestLogs() {
        if (requestLogs == null) {
            return new ArrayList<>();
        } else {
            return requestLogs;
        }
    }
}
