package com.motuma.paymentserviceesb.payment.service;

import com.motuma.paymentserviceesb.payment.dto.GenericSMSDto;
import com.motuma.paymentserviceesb.payment.dto.GenericSmsResponse;

public interface GenericSmsService {
    GenericSmsResponse sendGenericSms(GenericSMSDto genericSMSDto);
}
