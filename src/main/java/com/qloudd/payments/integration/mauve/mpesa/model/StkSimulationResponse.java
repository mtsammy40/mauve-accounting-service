package com.qloudd.payments.integration.mauve.mpesa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StkSimulationResponse {
    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;
    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;
    @JsonProperty("ResponseCode")
    private String responseCode;
    @JsonProperty("ResponseDescription")
    private String responseDescription;
    @JsonProperty("CustomerMessage")
    private String customerMessage;

    @Getter
    public enum ResponseCodes {
        OK("0"),
        UNKNOWN("Z");

        private String code;

        ResponseCodes(String code) {
            this.code = code;
        }

        static ResponseCodes resolve(String code) {
            return Arrays.stream(ResponseCodes.values())
                    .filter(responseCode -> responseCode.getCode().equals(code))
                    .findFirst()
                    .orElseGet(() -> ResponseCodes.UNKNOWN);
        }
    }

    public boolean isSuccess() {
        return ResponseCodes.resolve(this.responseCode).equals(ResponseCodes.OK);
    }
}
