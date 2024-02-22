package com.motuma.paymentserviceesb.payment.dto;

import com.motuma.paymentserviceesb.payment.configs.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodStatusDto {
    private String paymentMethodCode;
    private PaymentStatus paymentStatus;
}
