package com.motuma.paymentserviceesb.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpSendResonse {
    private String message;
    private String timeStamp;
    private int statusCode;
    private HttpStatus status;
}
