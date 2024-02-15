package com.motuma.paymentserviceesb.payment.service;

import com.motuma.paymentserviceesb.payment.dto.CoopTransactionDto;
import com.motuma.paymentserviceesb.payment.dto.CoopInternalTransactionResponse;
import org.springframework.stereotype.Service;

@Service
public interface CoopInternalTransactionService {
    CoopInternalTransactionResponse coopFundTransferInitiate(CoopTransactionDto coopRequest);
}
