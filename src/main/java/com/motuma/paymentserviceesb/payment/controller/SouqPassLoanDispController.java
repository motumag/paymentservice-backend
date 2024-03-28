package com.motuma.paymentserviceesb.payment.controller;

import com.motuma.paymentserviceesb.payment.dto.EbirrPaymentResponse;
import com.motuma.paymentserviceesb.payment.dto.SouqPassLoanDispDto;
import com.motuma.paymentserviceesb.payment.dto.SouqPassLoanDispResponse;
import com.motuma.paymentserviceesb.payment.service.SouqPassLoanDispursementService;
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
public class SouqPassLoanDispController {
    private final SouqPassLoanDispursementService souqPassLoanDispursementService;
    @PostMapping("/loanDispursement")
    public ResponseEntity<SouqPassLoanDispResponse> initateLoanDispursement(@RequestBody SouqPassLoanDispDto loanDispDto){
        try{
            SouqPassLoanDispResponse response=souqPassLoanDispursementService.initiateLoanDisp(loanDispDto);
            if (response.getStatusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }catch (Exception e){
            System.out.println("Controller exception: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    SouqPassLoanDispResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }


}
