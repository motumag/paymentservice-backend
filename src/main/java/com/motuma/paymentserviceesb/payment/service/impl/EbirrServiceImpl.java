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
            paymentDb.setTimestamp(timestampValue);
            paymentDb.setDebitAccountNumber(ebirrRequestDto.getDebitAccountNumber());
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
            serviceParams.put("transactionInfo", transactionInfo);
            requestPayload.put("serviceParams", serviceParams);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload.toString(), headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            EbirrTransactionResult transactionResult = objectMapper.readValue(responseBody, EbirrTransactionResult.class);
            String responseCode = transactionResult.getResponseCode();
            String errorCode = transactionResult.getErrorCode();
            String responseMsg = transactionResult.getResponseMsg();
            String txnTime = transactionResult.getTimestamp();

            EbirrPaymentResponse.EbirrPaymentResponseBuilder builder = EbirrPaymentResponse.builder()
                    .responseMsg(responseMsg)
                    .responseCode(responseCode)
                    .errorCode(errorCode)
                    .timeStamp(LocalDateTime.now().toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            if ("0".equals(errorCode) && "2001".equals(responseCode)) {
                if (transactionResult.getParams() != null) {
                    String state = transactionResult.getParams().getState();
                    if ("APPROVED".equals(state)) {
                        paymentDb.setResponseCode(responseCode);
                        paymentDb.setErrorCode(errorCode);
//                        paymentDb.setAccountNo(transactionResult.getParams().getAccountNo());
                        paymentDb.setAccountType(transactionResult.getParams().getAccountType());
                        paymentDb.setState(state);
                        paymentDb.setTransactionId(transactionResult.getParams().getTransactionId());
                        paymentDb.setIssuerTransactionId(transactionResult.getParams().getIssuerTransactionId());
                        paymentDb.setStatus(responseMsg);
                        paymentRepository.save(paymentDb);

                        builder.message("Ebirr USSD payment successfully done")
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .state(state)
                                .transactionTime(txnTime);
                    } else {
                        paymentDb.setEbirrRejectedOrderId(transactionResult.getParams().getOrderId());
                        paymentDb.setErrorDescription(transactionResult.getParams().getDescription());
                        paymentRepository.save(paymentDb);

                        builder.message("Ebirr payment STATE is not approved")
                                .rejectedOrderId(transactionResult.getParams().getOrderId())
                                .description(transactionResult.getParams().getDescription());
                    }
                }
            } else if (transactionResult.getParams() != null && transactionResult.getParams().getDescription() != null) {
                paymentDb.setEbirrRejectedOrderId(transactionResult.getParams().getOrderId());
                paymentDb.setErrorDescription(transactionResult.getParams().getDescription());
                paymentRepository.save(paymentDb);
                builder.message("Ebirr payment failed")
                        .rejectedOrderId(transactionResult.getParams().getOrderId())
                        .description(transactionResult.getParams().getDescription());
            }
            return builder.build();
        } catch (Exception e) {
            System.out.println("Ebirr ServiceImpl error: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
