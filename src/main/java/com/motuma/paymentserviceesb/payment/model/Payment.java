package com.motuma.paymentserviceesb.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String timestamp;
    private String amount;
    private String accountType;
    private String debitAccountNumber;
    private String creditAccountNumber;
    private String responseCode;
    private String errorCode;
    private String status;
    private String referenceId;
    private String invoiceId;
    private String state;
    private String transactionId;
    private String issuerTransactionId;
    private String paymentMethod;
    private String paymentServiceCode;
    private BigInteger paymentUserId;
    private String errorDescription;
    private String ebirrRejectedOrderId;
    private String paymentCompletionTime;
    private String paymentSourceName;
}

