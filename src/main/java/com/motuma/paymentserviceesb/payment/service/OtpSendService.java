package com.motuma.paymentserviceesb.payment.service;

import com.motuma.paymentserviceesb.payment.dto.OtpSendDto;
import com.motuma.paymentserviceesb.payment.dto.OtpSendResonse;
import org.springframework.stereotype.Service;

@Service
public interface OtpSendService {
    OtpSendResonse sendOtpToCustomerAccountNumber(OtpSendDto otpSendDto);
}
