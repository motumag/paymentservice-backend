package com.motuma.paymentserviceesb.payment.service.impl;
import com.motuma.paymentserviceesb.payment.dto.OtpSendDto;
import com.motuma.paymentserviceesb.payment.dto.OtpSendResonse;
import com.motuma.paymentserviceesb.payment.model.OtpSend;
import com.motuma.paymentserviceesb.payment.repository.SendOtpRepository;
import com.motuma.paymentserviceesb.payment.service.OtpSendService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OtpSendServiceImpl implements OtpSendService {
    private final SendOtpRepository sendOtpRepository;
    @Value("${SEND_OTP_CONFIG.serviceCode}")
    private String serviceCode;
    @Value("${SEND_OTP_CONFIG.channel}")
    private String channel;
    @Value("${SEND_OTP_CONFIG.Service_name}")
    private String serviceName;

    @Value("${ACE.sendOtpUrl}")
    private String sendOtpUrl;

    @Value("${ACE.phoneByAccount}")
    private String phoneByAccountUrl;


    @Override
    public OtpSendResonse sendOtpToCustomerAccountNumber(OtpSendDto otpSendDto) {
        try {
            JSONObject phoneByAccountNumberRequest = new JSONObject();
            JSONObject esbHeader = new JSONObject();
            esbHeader.put("serviceCode", serviceCode);
            esbHeader.put("channel", channel);
            esbHeader.put("Service_name", serviceName);
            esbHeader.put("Message_Id", "0000560rt");
            phoneByAccountNumberRequest.put("ESBHeader", esbHeader);
            JSONArray retrospectiveTypeArray = new JSONArray();
            JSONObject accountNumberObj = new JSONObject();
            accountNumberObj.put("accountNumber", otpSendDto.getAccountNumber());
            retrospectiveTypeArray.put(accountNumberObj);
            phoneByAccountNumberRequest.put("RETREIVEPHONEType", retrospectiveTypeArray);
            JSONObject finalRequest = new JSONObject();
            finalRequest.put("PhoneByAccountNumberRequest", phoneByAccountNumberRequest);

            OtpSend otpSendDb = new OtpSend();
            otpSendDb.setAccountNumber(otpSendDto.getAccountNumber());
            sendOtpRepository.save(otpSendDb);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RestTemplate restTemplate = new RestTemplate();
            String phoneEnqUrl = phoneByAccountUrl;
            HttpEntity<String> requestEntityPhoneEnquiry = new HttpEntity<>(finalRequest.toString(), headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(phoneEnqUrl, HttpMethod.POST, requestEntityPhoneEnquiry, String.class);
            String responseBody = responseEntity.getBody();
            JSONObject jsonObject = new JSONObject(responseBody);

            OtpSendResonse.OtpSendResonseBuilder phoneQueryOtpBuilder = OtpSendResonse.builder();
            JSONObject phoneEnquiryResponse = jsonObject.getJSONObject("RETREIVEPHONEResponse");

            if (phoneEnquiryResponse.has("Status")) {
                JSONObject statusObject = phoneEnquiryResponse.getJSONObject("Status");
                if (statusObject.has("successIndicator") && statusObject.getString("successIndicator").equals("Success")) {
                    String phoneNumberForOtp = phoneEnquiryResponse.getString("PhoneNumber");
                    String otpUrl = sendOtpUrl;
                    JSONObject mobileTextRequest = new JSONObject();
                    String generatedOtpNumber = generateAndRegisterOtp();
                    mobileTextRequest.put("Mobile", phoneNumberForOtp);
                    mobileTextRequest.put("Text", generatedOtpNumber);
                    otpSendDb.setPhoneNumber(phoneNumberForOtp);
                    otpSendDb.setOtpNumber(generatedOtpNumber);
                    sendOtpRepository.save(otpSendDb);

                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> requestEntityOtp = new HttpEntity<>(mobileTextRequest.toString(), headers);
                    ResponseEntity<String> otpResponse = restTemplate.exchange(otpUrl, HttpMethod.POST, requestEntityOtp, String.class);
                    String otpResponseBody = otpResponse.getBody();

                    JSONObject otpResponseObject = new JSONObject(otpResponseBody);
                    if (otpResponseObject.has("status")) {
                        String status = otpResponseObject.getString("status");
                        if ("Success".equals(status)) {
                            phoneQueryOtpBuilder.message("Request processed successfully.");
                            phoneQueryOtpBuilder.timeStamp(LocalDateTime.now().toString());
                            phoneQueryOtpBuilder.status(HttpStatus.OK);
                            phoneQueryOtpBuilder.statusCode(HttpStatus.OK.value());
                            otpSendDb.setStatus("Success");
                            otpSendDb.setResponseCode("200");
                            sendOtpRepository.save(otpSendDb);
                        } else {
                            phoneQueryOtpBuilder.message("Request failed.");
                            phoneQueryOtpBuilder.timeStamp(LocalDateTime.now().toString());
                            phoneQueryOtpBuilder.status(HttpStatus.BAD_REQUEST);
                            phoneQueryOtpBuilder.statusCode(HttpStatus.BAD_REQUEST.value());
                            otpSendDb.setStatus("Cancelled");
                            sendOtpRepository.save(otpSendDb);

                        }
                    } else {
                        phoneQueryOtpBuilder.message("Request failed");
                        phoneQueryOtpBuilder.timeStamp(LocalDateTime.now().toString());
                        phoneQueryOtpBuilder.status(HttpStatus.INTERNAL_SERVER_ERROR);
                        phoneQueryOtpBuilder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        otpSendDb.setStatus("Cancelled");
                        sendOtpRepository.save(otpSendDb);
                    }
                }
            }else {
                phoneQueryOtpBuilder.message("Request failed.");
                phoneQueryOtpBuilder.timeStamp(LocalDateTime.now().toString());
                phoneQueryOtpBuilder.status(HttpStatus.BAD_REQUEST);
                phoneQueryOtpBuilder.statusCode(HttpStatus.BAD_REQUEST.value());
                otpSendDb.setStatus("Cancelled");
                sendOtpRepository.save(otpSendDb);
            }
            return phoneQueryOtpBuilder.build();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    public String generateAndRegisterOtp() {
        int length = 6;
        Set<Integer> generated = new HashSet<>();
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        while (generated.size() < length) {
            int num = random.nextInt(10);
            if (!generated.contains(num)) {
                generated.add(num);
                sb.append(num);
            }
        }
        String randomString = sb.toString();
        return randomString;
    }
}
