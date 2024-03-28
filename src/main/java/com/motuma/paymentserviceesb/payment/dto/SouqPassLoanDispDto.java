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
public class SouqPassLoanDispDto {
    @NotBlank(message = "serviceCode is required")
    private String serviceCode;
    @NotBlank(message = "channel is required")
    private String channel;
    @NotBlank(message = "serviceName is required")
    private String serviceName;
    @NotNull(message = "OrderID must be supplied")
    @NotBlank(message = "OrderID must be supplied")
    private String ofsMessageId;
    @NotNull(message = "Credit Account Number must be supplied")
    @NotBlank(message = "Credit Account Number must be supplied")
    private String creditAccountNumber;
    @NotNull(message = "Amount must be supplied")
    @NotBlank(message = "Amount must be supplied")
    private String amount;
    @NotNull(message = "Payment source name must be supplied")
    @NotBlank(message = "Payment source name must be supplied")
    private String paymentSourceName;
    @NotNull(message = "Payment method code must be supplied")
    @NotBlank(message = "Payment method code must be supplied")
    private String paymentMethodCode;


}
