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
public class EbirrPaymentResponse {
    private String message;
    private String timeStamp;
    private int statusCode;
    private HttpStatus status;

    private String responseCode;
    private String transactionTime;
    private String errorCode;
    private String responseMsg;
    private String transactionId;
    private String issuerTransactionId;
    private String state;
    private String description;
    private String rejectedOrderId;
    private String referenceId;





}
