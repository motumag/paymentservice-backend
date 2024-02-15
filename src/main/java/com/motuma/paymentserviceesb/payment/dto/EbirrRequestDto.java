package com.motuma.paymentserviceesb.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EbirrRequestDto {
    @NotNull(message = "requestId should not be empty")
    @NotBlank(message = "requestId should not be empty")
    private String requestId;
    @NotNull(message = "creditAccountNumber should not be empty")
    @NotBlank(message = "creditAccountNumber should not be empty")
//    private String creditAccountNumber;
//    @NotNull(message = "debitAccountNumber should not be empty")
//    @NotBlank(message = "debitAccountNumber should not be empty")
    private String debitAccountNumber;
    @NotNull(message = "amount should not be empty")
    @NotBlank(message = "amount should not be empty")
    private String amount;
    @NotNull(message = "referenceId should not be empty")
    @NotBlank(message = "referenceId should not be empty")
    private String referenceId;
    @NotNull(message = "invoiceId should not be empty")
    @NotBlank(message = "invoiceId should not be empty")
    private String invoiceId;
    @NotNull(message = "paymentMethod should not be empty")
    @NotBlank(message = "paymentMethod should not be empty")
    private String paymentMethod;
    @NotNull(message = "paymentServiceCode should not be empty")
    @NotBlank(message = "paymentServiceCode should not be empty")
    private String paymentServiceCode;
    @NotBlank(message = "paymentSourceName is required")
    @NotNull(message = "paymentSourceName should not be empty")
    private String paymentSourceName;
}
