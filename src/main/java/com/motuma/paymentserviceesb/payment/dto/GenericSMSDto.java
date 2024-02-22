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
public class GenericSMSDto {
    @NotNull(message = "Phone number should not be empty")
    @NotBlank(message = "Phone number should not be empty")
    private String mobile;

    @NotNull(message = "Message should not be empty")
    @NotBlank(message = "Message should not be empty")
    private String text;
}
