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
public class SouqPassLoanDispResponse {
    private String message;
    private String timeStamp;
    private String transactionId;
    private String successIndicator;
    private String processingDate;
    private String errorType;
    private int statusCode;
    private HttpStatus status;
    private List<String> errorDescription;
}
