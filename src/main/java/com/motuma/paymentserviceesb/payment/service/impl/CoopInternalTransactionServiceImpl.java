package com.motuma.paymentserviceesb.payment.service.impl;

import com.motuma.paymentserviceesb.payment.dto.CoopTransactionDto;
import com.motuma.paymentserviceesb.payment.dto.CoopInternalTransactionResponse;
import com.motuma.paymentserviceesb.payment.model.OtpSend;
import com.motuma.paymentserviceesb.payment.model.Payment;
import com.motuma.paymentserviceesb.payment.repository.PaymentCategoryRepository;
import com.motuma.paymentserviceesb.payment.repository.PaymentRepository;
import com.motuma.paymentserviceesb.payment.repository.SendOtpRepository;
import com.motuma.paymentserviceesb.payment.service.CoopInternalTransactionService;
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
public class CoopInternalTransactionServiceImpl implements CoopInternalTransactionService {
    private final PaymentRepository paymentRepository;
    private final PaymentCategoryRepository paymentCategoryRepository;
    private final UserRepository userRepository;
    private final SendOtpRepository sendOtpRepository;
    @Value("${COOP_INTERNAL_FT.coopFtUrl}")
    private String coopFundTransferUrl;

    @Override
    @Transactional
    public CoopInternalTransactionResponse coopFundTransferInitiate(CoopTransactionDto coopRequest) {
        try {
            if (paymentRepository.existsByOrderId(coopRequest.getOfsMessageId())) {
                throw new RuntimeException("Transaction already exist with this messageId");
            }
            if (!paymentCategoryRepository.existsByPaymentCategoryCodeIgnoreCase(coopRequest.getPaymentMethodCode())) {
                throw new RuntimeException("There is no such kind of payment Method");
            }
            if (!paymentCategoryRepository.existsByPaymentSourceNameIgnoreCase(coopRequest.getPaymentSourceName())) {
                throw new RuntimeException("Please make sure your source is registered");
            }
            OtpSend otpSendConfirmation = sendOtpRepository.findOtpSendsByOtpNumberIgnoreCase(coopRequest.getOtpNumber());

            if (otpSendConfirmation == null) {
                throw new RuntimeException("OTP number is not found");
            }
            String otpNumberFromDb = otpSendConfirmation.getOtpNumber();
            String accountNumberFromOtp = otpSendConfirmation.getAccountNumber();
            String otpStatus = otpSendConfirmation.getStatus();
            System.out.println("OPT STATUS IS : "+otpStatus);

            if (!otpNumberFromDb.equals(coopRequest.getOtpNumber())) {
                throw new RuntimeException("Otp Mismatch");
            }

            if (!accountNumberFromOtp.equals(coopRequest.getDebitAccountNumber())) {
                throw new RuntimeException("Account Number Mismatch");
            }
            switch (otpStatus) {
                case "Confirmed" -> throw new RuntimeException("The OTP you provided has already been used");
                case "Failure" -> throw new RuntimeException("Forbidden to use this OTP");
                case "Success" -> {
                    otpSendConfirmation.setStatus("Confirmed");
                    sendOtpRepository.save(otpSendConfirmation);
                }
            }

            CurrentLoggedInUser currentLoggedInUser = new CurrentLoggedInUser();
            Payment coopPaymentDb = new Payment();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime currentDateTime = LocalDateTime.now();
            String timestampValue = currentDateTime.format(formatter);
            //save the request to db here
            coopPaymentDb.setOrderId(coopRequest.getOfsMessageId());
            coopPaymentDb.setTimestamp(timestampValue);
            coopPaymentDb.setDebitAccountNumber(coopRequest.getDebitAccountNumber());
            coopPaymentDb.setCreditAccountNumber(coopRequest.getCreditAccountNumber());
            coopPaymentDb.setAmount(coopRequest.getDebitAmount());
            coopPaymentDb.setPaymentMethod("COOP_FT");
            coopPaymentDb.setPaymentServiceCode(coopRequest.getPaymentMethodCode());
            coopPaymentDb.setPaymentSourceName(coopRequest.getPaymentSourceName());
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
            String ftUrl = coopFundTransferUrl;

            JSONObject requestPayload = new JSONObject();
            JSONObject paymentServiceTransferRequest = new JSONObject();
            JSONObject esbHeader = new JSONObject();
            JSONObject ofsFunction = new JSONObject();
            JSONObject fundsTransferActRMMTType = new JSONObject();
            //TODO: The ESBHeader part
            esbHeader.put("serviceCode", coopRequest.getServiceCode());
            esbHeader.put("channel", coopRequest.getChannel());
            esbHeader.put("Service_name", coopRequest.getServiceName());
            esbHeader.put("Message_Id", coopRequest.getOfsMessageId());
            //TODO: The Ofs part
            ofsFunction.put("messageId", coopRequest.getOfsMessageId());
            //TODO: The FUNDSTRANSFERACTRMMTType part
            fundsTransferActRMMTType.put("DebitAmount", coopRequest.getDebitAmount());
            fundsTransferActRMMTType.put("DebitAccount", coopRequest.getDebitAccountNumber());
            fundsTransferActRMMTType.put("CreditAccount", coopRequest.getCreditAccountNumber());

            paymentServiceTransferRequest.put("ESBHeader", esbHeader);
            paymentServiceTransferRequest.put("OfsFunction", ofsFunction);
            paymentServiceTransferRequest.put("FUNDSTRANSFERACTRMMTType", fundsTransferActRMMTType);
            requestPayload.put("PaymentServiceTransferRequest", paymentServiceTransferRequest);
            String requestBody = requestPayload.toString();
            System.out.println("Request body: " + requestBody);

            HttpEntity<String> requestEntityCoopFt = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(ftUrl, HttpMethod.POST, requestEntityCoopFt, String.class);

            String responseBody = responseEntity.getBody();
            JSONObject jsonObject = new JSONObject(responseBody);
            CoopInternalTransactionResponse.CoopInternalTransactionResponseBuilder builder = CoopInternalTransactionResponse.builder();
            JSONObject paymentServiceResponse = jsonObject.getJSONObject("PaymentServiceResponse");

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
                coopPaymentDb.setErrorDescription(esbStatus.getString("errorType"));
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
                    builder.errorDescription(errorDescriptions);
                } else {
                    builder.errorDescription(Collections.emptyList());
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
