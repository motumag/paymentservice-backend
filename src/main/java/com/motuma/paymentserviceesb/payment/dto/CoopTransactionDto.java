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
public class CoopTransactionDto {
@NotBlank(message = "serviceCode is required")
private String serviceCode;

   @NotBlank(message = "channel is required")
   private String channel;

   @NotBlank(message = "serviceName is required")
   private String serviceName;

   @NotBlank(message = "ofsMessageId is required")
   private String ofsMessageId;

   @NotNull(message = "debitAmount is required")
   @NotBlank(message = "debitAmount is required")
   private String debitAmount;
//   @NotNull(message = "creditAccountNumber should not be empty")
//   @NotBlank(message = "creditAccountNumber should not be empty")
   private String creditAccountNumber;
   @NotNull(message = "debitAccountNumber should not be empty")
   @NotBlank(message = "debitAccountNumber should not be empty")
   private String debitAccountNumber;
   @NotBlank(message = "paymentMethodCode is required")
   @NotNull(message = "debitAccountNumber should not be empty")
   private String paymentMethodCode;
   @NotBlank(message = "paymentSourceName is required")
   @NotNull(message = "paymentSourceName should not be empty")
   private String paymentSourceName;

   @NotBlank(message = "otpNumber is required")
   @NotNull(message = "otpNumber should not be empty")
   private String otpNumber;
}
