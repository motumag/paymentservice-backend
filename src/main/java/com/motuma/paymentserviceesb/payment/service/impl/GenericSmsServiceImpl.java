package com.motuma.paymentserviceesb.payment.service.impl;

import com.motuma.paymentserviceesb.payment.dto.GenericSMSDto;
import com.motuma.paymentserviceesb.payment.dto.GenericSmsResponse;
import com.motuma.paymentserviceesb.payment.service.GenericSmsService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GenericSmsServiceImpl implements GenericSmsService {
    @Value("${ACE.sendGenericOtpUrl}")
    private String genericOtpUrl;
    @Override
    public GenericSmsResponse sendGenericSms(GenericSMSDto genericSMSDto) {
       try{
           String sendToGenericOtpUrl = genericOtpUrl;
           JSONObject mobileTextRequest = new JSONObject();
           String phoneNumber = genericSMSDto.getMobile();
           String otpNumber = genericSMSDto.getText();
           mobileTextRequest.put("Mobile", phoneNumber);
           mobileTextRequest.put("Text", otpNumber);

           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           RestTemplate restTemplate= new RestTemplate();
           HttpEntity<String> requestEntityOtp = new HttpEntity<>(mobileTextRequest.toString(), headers);
           ResponseEntity<String> otpResponse = restTemplate.exchange(sendToGenericOtpUrl, HttpMethod.POST, requestEntityOtp, String.class);
           String otpResponseBody = otpResponse.getBody();
           GenericSmsResponse.GenericSmsResponseBuilder otpResponseBuilder=GenericSmsResponse.builder()
                   ;        JSONObject otpResponseObject = new JSONObject(otpResponseBody);
           if (otpResponseObject.has("status")) {
               String status = otpResponseObject.getString("status");
               if ("Success".equals(status)) {
                   otpResponseBuilder.message("Request processed successfully.");
                   otpResponseBuilder.timeStamp(LocalDateTime.now().toString());
                   otpResponseBuilder.status(HttpStatus.OK);
                   otpResponseBuilder.statusCode(HttpStatus.OK.value());
               } else {
                   otpResponseBuilder.message("Request failed.");
                   otpResponseBuilder.timeStamp(LocalDateTime.now().toString());
                   otpResponseBuilder.status(HttpStatus.BAD_REQUEST);
                   otpResponseBuilder.statusCode(HttpStatus.BAD_REQUEST.value());

               }
           } else {
               otpResponseBuilder.message("Request failed");
               otpResponseBuilder.timeStamp(LocalDateTime.now().toString());
               otpResponseBuilder.status(HttpStatus.INTERNAL_SERVER_ERROR);
               otpResponseBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
           }
           return otpResponseBuilder.build();
       }catch (Exception e){
           System.out.println(e.getMessage());
           throw new RuntimeException(e.getMessage());
       }
    }
}
