package com.motuma.paymentserviceesb.payment.controller;

import com.motuma.paymentserviceesb.payment.dto.CoopTransactionDto;
import com.motuma.paymentserviceesb.payment.dto.CoopInternalTransactionResponse;
import com.motuma.paymentserviceesb.payment.service.CoopInternalTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class CoopInternalTransactionController {
    private final CoopInternalTransactionService coopPaymentService;
    @PostMapping("/internal/fundtransfer")
    public ResponseEntity<CoopInternalTransactionResponse> coopInitiateFt(@Valid @RequestBody CoopTransactionDto coopTransactionDto){
        try {
            return ResponseEntity.ok(coopPaymentService.coopFundTransferInitiate(coopTransactionDto));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    CoopInternalTransactionResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

}
