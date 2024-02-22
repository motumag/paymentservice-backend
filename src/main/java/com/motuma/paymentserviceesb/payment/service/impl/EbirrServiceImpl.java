package com.motuma.paymentserviceesb.payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motuma.paymentserviceesb.payment.dto.EbirrPaymentResponse;
import com.motuma.paymentserviceesb.payment.dto.EbirrRequestDto;
import com.motuma.paymentserviceesb.payment.dto.EbirrTransactionResult;
import com.motuma.paymentserviceesb.payment.model.Payment;
import com.motuma.paymentserviceesb.payment.repository.PaymentCategoryRepository;
import com.motuma.paymentserviceesb.payment.repository.PaymentRepository;
import com.motuma.paymentserviceesb.payment.service.EbirrService;
import com.motuma.paymentserviceesb.security.config.CurrentLoggedInUser;
import com.motuma.paymentserviceesb.security.model.User;
import com.motuma.paymentserviceesb.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EbirrServiceImpl implements EbirrService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentCategoryRepository paymentCategoryRepository;
    @Value("${EBIRR.url}")
    private String ebirrUrl;

    @Value("${EBIRR.schemaVersion}")
    private String schemaVersion;

    @Value("${EBIRR.channelName}")
    private String channelName;

    @Value("${EBIRR.serviceName}")
    private String serviceName;

    @Value("${EBIRR.merchantUid}")
    private String merchantUid;

    @Value("${EBIRR.paymentMethod}")
    private String paymentMethod;

    @Value("${EBIRR.apiKey}")
    private String apiKey;

    @Value("${EBIRR.apiUserId}")
    private String apiUserId;

    @Value("${EBIRR.currency}")
    private String currency;

    @Value("${EBIRR.description}")
    private String description;

    @Override
    @Transactional
    public EbirrPaymentResponse createEbirrTransaction(EbirrRequestDto ebirrRequestDto) {
        try {
            if (!paymentCategoryRepository.existsByPaymentCategoryCodeAndPaymentSourceNameIgnoreCase(
                    ebirrRequestDto.getPaymentServiceCode(),
                    ebirrRequestDto.getPaymentSourceName())) {
                throw new RuntimeException("Please, check your paymentCategoryCode and PaymentSourceName");
            }
            if (paymentRepository.existsByOrderId(
                    ebirrRequestDto.getRequestId())) {
                throw new RuntimeException("Duplicate order, transaction is already exist");
            }
            if (paymentRepository.existsByReferenceIdOrInvoiceIdIgnoreCase(
                    ebirrRequestDto.getReferenceId(),
                    ebirrRequestDto.getInvoiceId())) {
                throw new RuntimeException("Duplicate referenceId or invoiceId is not allowed");
            }
            CurrentLoggedInUser currentLoggedInUser = new CurrentLoggedInUser();
            Payment paymentDb = new Payment();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime currentDateTime = LocalDateTime.now();
            String timestampValue = currentDateTime.format(formatter);
            //save the request to db here
            paymentDb.setOrderId(ebirrRequestDto.getRequestId());
            paymentDb.setReferenceId(ebirrRequestDto.getReferenceId());
            paymentDb.setInvoiceId(ebirrRequestDto.getInvoiceId());
            paymentDb.setTimestamp(timestampValue);
            paymentDb.setPaymentSourceName(ebirrRequestDto.getPaymentSourceName());
            paymentDb.setDebitAccountNumber(ebirrRequestDto.getDebitAccountNumber());
            paymentDb.setAccountType("MWALLET_ACCOUNT");
            paymentDb.setCreditAccountNumber(merchantUid);
            paymentDb.setAmount(ebirrRequestDto.getAmount());
            paymentDb.setPaymentMethod(ebirrRequestDto.getPaymentMethod());
            paymentDb.setPaymentServiceCode(ebirrRequestDto.getPaymentServiceCode());
            paymentDb.setStatus("Pending");

            Optional<User> userOptional = userRepository.findByUserName(currentLoggedInUser.getCurrentUserSub());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                BigInteger userId = BigInteger.valueOf(user.getUserId());
                paymentDb.setPaymentUserId(userId);
            } else {
                throw new RuntimeException("Something wrong with logged in user");
            }
            paymentRepository.save(paymentDb);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = ebirrUrl;
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("schemaVersion", schemaVersion);
            requestPayload.put("requestId", ebirrRequestDto.getRequestId());
            requestPayload.put("timestamp", timestampValue);
            requestPayload.put("channelName", channelName);
            requestPayload.put("serviceName", serviceName);
            // Create the serviceParams JSON object
            JSONObject serviceParams = new JSONObject();
            serviceParams.put("merchantUid", merchantUid);
            serviceParams.put("paymentMethod", paymentMethod);
            serviceParams.put("apiKey", apiKey);
            serviceParams.put("apiUserId", apiUserId);

            JSONObject payerInfo = new JSONObject();
            payerInfo.put("accountNo", ebirrRequestDto.getDebitAccountNumber());
            serviceParams.put("payerInfo", payerInfo);

            JSONObject transactionInfo = new JSONObject();
            transactionInfo.put("amount", ebirrRequestDto.getAmount());
            transactionInfo.put("currency", currency);
            transactionInfo.put("description", description);
            transactionInfo.put("referenceId", ebirrRequestDto.getReferenceId());
            transactionInfo.put("invoiceId", ebirrRequestDto.getInvoiceId());
            requestPayload.put("serviceParams", serviceParams);
            serviceParams.put("transactionInfo", transactionInfo);
            System.out.println("request body: "+requestPayload.toString());

            HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload.toString(), headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();

            JSONObject jsonObject = new JSONObject(responseBody);

            EbirrPaymentResponse.EbirrPaymentResponseBuilder responseBuilder=EbirrPaymentResponse.builder();

            String timestamp = jsonObject.getString("timestamp");
            String requestId = jsonObject.getString("requestId");
            String sessionId = jsonObject.optString("sessionId");
            String responseCode = jsonObject.getString("responseCode");
            String errorCode = jsonObject.getString("errorCode");
            String responseMsg = jsonObject.getString("responseMsg");

            if (jsonObject.has("responseCode") && jsonObject.getString("responseCode").equals("2001")) {
                JSONObject params = jsonObject.getJSONObject("params");
                if (params.has("state") && params.getString("state").equals("APPROVED")) {
                    paymentDb.setResponseCode(responseCode);
                    paymentDb.setTransactionId(params.getString("transactionId"));
                    paymentDb.setIssuerTransactionId(params.getString("issuerTransactionId"));
                    paymentDb.setErrorCode("0");
                    paymentDb.setStatus(responseMsg);
                    paymentDb.setState(params.getString("state"));
                    paymentDb.setPaymentCompletionTime(timestamp);
                    paymentRepository.save(paymentDb);

                    responseBuilder.message("Ebirr Payment completed successfully");
                    responseBuilder.timeStamp(timestamp);
//                    responseBuilder.status(HttpStatus.OK);
                    responseBuilder.status(HttpStatus.OK);
                    responseBuilder.statusCode(HttpStatus.OK.value());
                    responseBuilder.transactionId(params.getString("transactionId"));
                    responseBuilder.issuerTransactionId(params.getString("issuerTransactionId"));
                    responseBuilder.state(params.getString("state"));
                    responseBuilder.errorCode(errorCode);
                    responseBuilder.responseMsg(responseMsg);
                    responseBuilder.state(params.getString("state"));
                }else {
                    paymentDb.setErrorCode(errorCode);
                    paymentDb.setErrorDescription(responseMsg);
                    paymentRepository.save(paymentDb);
                    responseBuilder.message("Ebirr Payment failed");
                    responseBuilder.timeStamp(timestamp);
                    responseBuilder.status(HttpStatus.BAD_REQUEST);
                    responseBuilder.statusCode(HttpStatus.BAD_REQUEST.value());
                    responseBuilder.state("rejected");
                    responseBuilder.errorCode(errorCode);
                    responseBuilder.responseMsg(responseMsg);
                }

                }else {
                paymentDb.setErrorCode(errorCode);
                paymentDb.setErrorDescription(responseMsg);
                paymentRepository.save(paymentDb);
                responseBuilder.message("Transaction Failed");
                responseBuilder.timeStamp(timestamp);
                responseBuilder.status(HttpStatus.BAD_REQUEST);
                responseBuilder.statusCode(HttpStatus.BAD_REQUEST.value());
                responseBuilder.state("rejected");
                responseBuilder.errorCode(errorCode);
                responseBuilder.responseMsg(responseMsg);
            }
            return responseBuilder.build();

        } catch (Exception e) {
            System.out.println("Ebirr ServiceImpl error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
