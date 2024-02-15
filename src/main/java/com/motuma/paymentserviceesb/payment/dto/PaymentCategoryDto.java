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
public class PaymentCategoryDto {
    @NotBlank(message = "paymentCategoryCode is required")
    @NotNull(message = "paymentCategoryCode should not be empty")
    private String paymentCategoryCode;
    @NotNull(message = "paymentCategoryName should not be empty")
    @NotBlank(message = "paymentCategoryName is required")
    private String paymentCategoryName;
    @NotNull(message = "paymentSourceName should not be empty")
    @NotBlank(message = "paymentSourceName is required")
    private String paymentSourceName;
}
