package com.motuma.paymentserviceesb.payment.service;

import com.motuma.paymentserviceesb.payment.dto.PaymentCategoryDto;
import com.motuma.paymentserviceesb.payment.dto.PaymentCategoryResponse;
import com.motuma.paymentserviceesb.payment.model.PaymentCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentCategoryService {
List<PaymentCategory> listOfAvailablePaymentTypes();
PaymentCategoryResponse createPaymentCategory(PaymentCategoryDto paymentCategoryDto);
}
