package com.motuma.paymentserviceesb.payment.controller;

import com.motuma.paymentserviceesb.payment.dto.EbirrPaymentResponse;
import com.motuma.paymentserviceesb.payment.dto.EbirrRequestDto;
import com.motuma.paymentserviceesb.payment.service.EbirrService;
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
public class EbirrController {
    private final EbirrService ebirrService;
    @PostMapping("/ebirr")
    public ResponseEntity<EbirrPaymentResponse> createEbirrTransactionPayment(@Valid @RequestBody EbirrRequestDto ebirrRequestDto){
        try {
            EbirrPaymentResponse response = ebirrService.createEbirrTransaction(ebirrRequestDto);
            if (response.getStatusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }catch (Exception e){
            System.out.println("Controller exception: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    EbirrPaymentResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }
}
