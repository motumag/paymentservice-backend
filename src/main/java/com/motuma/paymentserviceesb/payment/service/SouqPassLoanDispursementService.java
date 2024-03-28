package com.motuma.paymentserviceesb.payment.service;

import com.motuma.paymentserviceesb.payment.dto.SouqPassLoanDispDto;
import com.motuma.paymentserviceesb.payment.dto.SouqPassLoanDispResponse;
import org.springframework.stereotype.Service;

@Service
public interface SouqPassLoanDispursementService {
    SouqPassLoanDispResponse initiateLoanDisp(SouqPassLoanDispDto dispRequest);
}
