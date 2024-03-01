package com.motuma.paymentserviceesb.payment.controller;

import com.motuma.paymentserviceesb.payment.dto.CoopInternalTransactionResponse;
import com.motuma.paymentserviceesb.payment.dto.EbirrPaymentResponse;
import com.motuma.paymentserviceesb.payment.dto.OtpSendDto;
import com.motuma.paymentserviceesb.payment.dto.OtpSendResonse;
import com.motuma.paymentserviceesb.payment.service.OtpSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class SendOtpController {
    private final OtpSendService otpSendService;
    @PostMapping("/send")
    public ResponseEntity<OtpSendResonse> sendOtp(@RequestBody OtpSendDto otpSendDto){
        try{
            OtpSendResonse response = otpSendService.sendOtpToCustomerAccountNumber(otpSendDto);
            if (response.getStatusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    OtpSendResonse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }
}
