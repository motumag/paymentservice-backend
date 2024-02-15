package com.motuma.paymentserviceesb.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoopInternalTransactionResponse {
    private String message;
    private String timeStamp;
    private int statusCode;
    private HttpStatus status;

    private String transactionId;
    private String successIndicator;
    private String processingDate;
    private String errorType;
    private List<String> errorDescription;
}
