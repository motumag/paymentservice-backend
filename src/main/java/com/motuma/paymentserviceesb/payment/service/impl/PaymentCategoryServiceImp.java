package com.motuma.paymentserviceesb.payment.service.impl;

import com.motuma.paymentserviceesb.payment.dto.PaymentCategoryDto;
import com.motuma.paymentserviceesb.payment.dto.PaymentCategoryResponse;
import com.motuma.paymentserviceesb.payment.model.PaymentCategory;
import com.motuma.paymentserviceesb.payment.repository.PaymentCategoryRepository;
import com.motuma.paymentserviceesb.payment.service.PaymentCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCategoryServiceImp implements PaymentCategoryService {
    private final PaymentCategoryRepository paymentCategoryRepository;
    @Override
    public List<PaymentCategory> listOfAvailablePaymentTypes() {
        try{
            return paymentCategoryRepository.findAll();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PaymentCategoryResponse createPaymentCategory(PaymentCategoryDto paymentCategoryDto) {
       try {
           if(paymentCategoryRepository
                   .existsByPaymentCategoryCodeIgnoreCase(paymentCategoryDto.getPaymentCategoryCode())){
               throw new RuntimeException("Payment Category exist by this code");
           }
           PaymentCategory paymentCategory=new PaymentCategory();
           paymentCategory.setPaymentCategoryCode(paymentCategoryDto.getPaymentCategoryCode());
           paymentCategory.setPaymentCategoryName(paymentCategoryDto.getPaymentCategoryName());
           paymentCategory.setPaymentSourceName(paymentCategoryDto.getPaymentSourceName());
           paymentCategoryRepository.save(paymentCategory);
           return PaymentCategoryResponse.builder()
                   .message("Payment Category is created successfully")
                   .timeStamp(LocalDateTime.now().toString())
                   .status(HttpStatus.OK)
                   .statusCode(HttpStatus.OK.value())
                   .build();
       }catch (Exception e){
           throw new RuntimeException(e.getMessage());
       }
    }
}
