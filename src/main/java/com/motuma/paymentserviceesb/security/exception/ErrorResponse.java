package com.motuma.paymentserviceesb.security.exception;

import lombok.Data;

import java.util.List;
@Data
public class ErrorResponse {
    private List<String> errors;
    private String timestamp;
    private int status;
}
