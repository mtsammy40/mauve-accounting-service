package com.qloudd.payments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RequestLog {
    LocalDateTime initiated;
    LocalDateTime completed;
    Object request;
    Object response;
    int attempt;
    int responseCode;
}
