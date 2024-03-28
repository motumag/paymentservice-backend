package com.motuma.paymentserviceesb.payment.service.impl;

import com.motuma.paymentserviceesb.payment.dto.CoopInternalTransactionResponse;
import com.motuma.paymentserviceesb.payment.dto.SouqPassLoanDispDto;
import com.motuma.paymentserviceesb.payment.dto.SouqPassLoanDispResponse;
import com.motuma.paymentserviceesb.payment.model.Payment;
import com.motuma.paymentserviceesb.payment.repository.PaymentCategoryRepository;
import com.motuma.paymentserviceesb.payment.repository.PaymentRepository;
import com.motuma.paymentserviceesb.payment.service.SouqPassLoanDispursementService;
import com.motuma.paymentserviceesb.security.config.CurrentLoggedInUser;
import com.motuma.paymentserviceesb.security.model.User;
import com.motuma.paymentserviceesb.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SouqPassLoanDispServiceImp implements SouqPassLoanDispursementService {
    private final PaymentRepository paymentRepository;
    private final PaymentCategoryRepository paymentCategoryRepository;
    private final UserRepository userRepository;
    @Value("${SOUQPASS_REPAYMENT.souqpassRePaymentFTUrl}")
    private String souqpassLoanDispUrl;

    @Override
    @Transactional
    public SouqPassLoanDispResponse initiateLoanDisp(SouqPassLoanDispDto dispRequest) {
        try {
            if (paymentRepository.existsByOrderId(dispRequest.getOfsMessageId())) {
                throw new RuntimeException("Transaction already exist with this messageId");
            }
            if (!paymentCategoryRepository.existsByPaymentCategoryCodeAndPaymentSourceNameIgnoreCase(
                    dispRequest.getPaymentMethodCode(),
                    dispRequest.getPaymentSourceName())) {
                throw new RuntimeException("Please, check your paymentCategoryCode and PaymentSourceName");
            }
            CurrentLoggedInUser currentLoggedInUser = new CurrentLoggedInUser();
            Payment coopPaymentDb = new Payment();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime currentDateTime = LocalDateTime.now();
            String timestampValue = currentDateTime.format(formatter);

            coopPaymentDb.setOrderId(dispRequest.getOfsMessageId());
            coopPaymentDb.setTimestamp(timestampValue);
            coopPaymentDb.setCreditAccountNumber(dispRequest.getCreditAccountNumber());
            coopPaymentDb.setAmount(dispRequest.getAmount());
            coopPaymentDb.setPaymentMethod("COOP_FT");
            coopPaymentDb.setPaymentServiceCode(dispRequest.getPaymentMethodCode());
            coopPaymentDb.setPaymentSourceName(dispRequest.getPaymentSourceName());
            coopPaymentDb.setStatus("Pending");

            Optional<User> userOptional = userRepository.findByUserName(currentLoggedInUser.getCurrentUserSub());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                BigInteger userId = BigInteger.valueOf(user.getUserId());
                coopPaymentDb.setPaymentUserId(userId);
            } else {
                throw new RuntimeException("Something wrong with logged in user");
            }
            paymentRepository.save(coopPaymentDb);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String dispUrl = souqpassLoanDispUrl;

            JSONObject requestPayload = new JSONObject();
            JSONObject souqPassDispRequest = new JSONObject();
            JSONObject esbHeader = new JSONObject();
            JSONObject ofsFunction = new JSONObject();
            JSONObject fundsTransferActRMMTType = new JSONObject();
            //TODO: The ESBHeader part
            esbHeader.put("serviceCode", dispRequest.getServiceCode());
            esbHeader.put("channel", dispRequest.getChannel());
            esbHeader.put("Service_name", dispRequest.getServiceName());
            esbHeader.put("Message_Id", dispRequest.getOfsMessageId());
            //TODO: The Ofs part
            ofsFunction.put("messageId", dispRequest.getOfsMessageId());
            //TODO: The FUNDSTRANSFERACTRMMTType part
            fundsTransferActRMMTType.put("creditAccountNumber", dispRequest.getCreditAccountNumber());
            fundsTransferActRMMTType.put("amount", dispRequest.getAmount());

            souqPassDispRequest.put("ESBHeader", esbHeader);
            souqPassDispRequest.put("OfsFunction", ofsFunction);
            souqPassDispRequest.put("FUNDSTRANSFERACTRMMTType", fundsTransferActRMMTType);
            requestPayload.put("SouqPassDispRequest", souqPassDispRequest);
            String requestBody = requestPayload.toString();
            System.out.println("Request body: " + requestBody);
            HttpEntity<String> requestEntityCoopFt = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(dispUrl, HttpMethod.POST, requestEntityCoopFt, String.class);
            String responseBody = responseEntity.getBody();
            System.out.println("response body is: " + responseBody);

            JSONObject jsonObject = new JSONObject(responseBody);
            SouqPassLoanDispResponse.SouqPassLoanDispResponseBuilder builder = SouqPassLoanDispResponse.builder();

            JSONObject paymentServiceResponse = jsonObject.getJSONObject("SouqpassDispResponse");
            JSONObject esbStatus = paymentServiceResponse.getJSONObject("ESBStatus");
            String responseCode = esbStatus.getString("responseCode");
            if (paymentServiceResponse.has("Status")) {
                JSONObject statusObject = paymentServiceResponse.getJSONObject("Status");
                if (statusObject.has("successIndicator") && statusObject.getString("successIndicator").equals("Success")) {
                    JSONObject fundTransferType = paymentServiceResponse.getJSONObject("FUNDSTRANSFERType");
//                    Update the success filed to database
                    coopPaymentDb.setResponseCode(responseCode);
                    coopPaymentDb.setErrorCode("0");
                    coopPaymentDb.setAccountType(fundTransferType.getString("TRANSACTIONTYPE"));
                    coopPaymentDb.setState(statusObject.getString("successIndicator"));
                    coopPaymentDb.setTransactionId(fundTransferType.getString("id"));
                    coopPaymentDb.setIssuerTransactionId(statusObject.getString("transactionId"));
                    coopPaymentDb.setStatus(statusObject.getString("successIndicator"));
                    coopPaymentDb.setCreditAccountNumber(fundTransferType.getString("CREDITACCTNO"));
                    coopPaymentDb.setPaymentCompletionTime(fundTransferType.getString("PROCESSINGDATE"));
                    paymentRepository.save(coopPaymentDb);
//                    Build the success response body
                    builder.message("Internal fund transfer completed");
                    builder.timeStamp(LocalDateTime.now().toString());
                    builder.status(HttpStatus.OK);
                    builder.statusCode(HttpStatus.OK.value());
                    builder.transactionId(statusObject.getString("transactionId"));
                    builder.successIndicator(statusObject.getString("successIndicator"));
                    builder.processingDate(fundTransferType.getString("PROCESSINGDATE"));
                }
            } else {
                coopPaymentDb.setResponseCode(esbStatus.getString("responseCode"));
                coopPaymentDb.setErrorCode(esbStatus.getString("errorType"));
                coopPaymentDb.setStatus(esbStatus.getString("Status"));
                paymentRepository.save(coopPaymentDb);

                builder.message("Internal fund transfer failed");
                builder.timeStamp(LocalDateTime.now().toString());
                builder.status(HttpStatus.BAD_REQUEST);
                builder.statusCode(HttpStatus.BAD_REQUEST.value());
                builder.successIndicator(esbStatus.getString("Status"));
                builder.errorType(esbStatus.getString("errorType"));
                if (esbStatus.has("errorDescription")) {
                    JSONArray errorDescriptionArray = esbStatus.getJSONArray("errorDescription");
                    List<String> errorDescriptions = errorDescriptionArray.toList().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());
                    String firstErrorDescription = errorDescriptionArray.getString(0);
                    coopPaymentDb.setErrorDescription(firstErrorDescription);
                    paymentRepository.save(coopPaymentDb);
                    builder.errorDescription(errorDescriptions);
                } else {
                    builder.errorDescription(Collections.emptyList());
                }
            }
            return builder.build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());

        }
    }
}
