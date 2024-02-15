package com.motuma.paymentserviceesb.payment.controller;
import com.motuma.paymentserviceesb.payment.dto.PaymentCategoryDto;
import com.motuma.paymentserviceesb.payment.dto.PaymentCategoryResponse;
import com.motuma.paymentserviceesb.payment.model.PaymentCategory;
import com.motuma.paymentserviceesb.payment.service.PaymentCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentCategoryController {
    private final PaymentCategoryService paymentCategoryService;
    @PostMapping("/createPaymentCategory")
    public ResponseEntity<PaymentCategoryResponse> createPaymentCategory(@Valid @RequestBody PaymentCategoryDto paymentCategoryDto){
        try {
            return ResponseEntity.ok(paymentCategoryService.createPaymentCategory(paymentCategoryDto));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    PaymentCategoryResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
    }
    }
    @GetMapping("/listAllPaymentType")
    public ResponseEntity<List<PaymentCategory>> listAllOfPaymentType(){
        try {
            List<PaymentCategory> paymentCategories = paymentCategoryService.listOfAvailablePaymentTypes();
            return ResponseEntity.ok(paymentCategories);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }
}
