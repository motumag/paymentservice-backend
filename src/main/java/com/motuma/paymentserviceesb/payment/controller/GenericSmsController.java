package com.motuma.paymentserviceesb.payment.controller;

import com.motuma.paymentserviceesb.payment.dto.EbirrPaymentResponse;
import com.motuma.paymentserviceesb.payment.dto.GenericSMSDto;
import com.motuma.paymentserviceesb.payment.dto.GenericSmsResponse;
import com.motuma.paymentserviceesb.payment.service.GenericSmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/genericSms")
@RequiredArgsConstructor
public class GenericSmsController {
    private final GenericSmsService genericSmsService;
    @PostMapping("/send")
    public ResponseEntity<GenericSmsResponse> sendGenericSms(@RequestBody GenericSMSDto genericSMSDto){
        try {
          GenericSmsResponse genericSmsResponse=genericSmsService.sendGenericSms(genericSMSDto);
            if (genericSmsResponse.getStatusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok(genericSmsResponse);
            } else {
                return ResponseEntity.badRequest().body(genericSmsResponse);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Controller exception: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    GenericSmsResponse.builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }

    }
}
