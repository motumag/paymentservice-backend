package com.motuma.paymentserviceesb.payment.service;

import com.motuma.paymentserviceesb.payment.dto.EbirrPaymentResponse;
import com.motuma.paymentserviceesb.payment.dto.EbirrRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface EbirrService {
    EbirrPaymentResponse createEbirrTransaction(EbirrRequestDto ebirrRequestDto);
}
