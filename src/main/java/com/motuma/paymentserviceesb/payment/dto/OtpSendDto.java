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
public class OtpSendDto {
    @NotNull(message = "accountNumber should not be empty")
    @NotBlank(message = "accountNumber should not be empty")
    private String accountNumber;
}
