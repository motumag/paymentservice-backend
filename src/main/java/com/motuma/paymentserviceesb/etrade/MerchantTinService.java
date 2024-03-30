package com.motuma.paymentserviceesb.etrade;

import com.motuma.paymentserviceesb.config.HttpProcessor;
import com.motuma.paymentserviceesb.security.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.RequestBuilder;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MerchantTinService {
    private String URL = "https://etrade.gov.et/api/Registration/GetRegistrationInfoByTin/";
    private String refererHeader = "https://etrade.gov.et/business-license-checker";
    private final HttpProcessor httpProcessor;
    public MerchantTinService(HttpProcessor httpProcessor) {
        this.httpProcessor = httpProcessor;
    }
    public ResponseEntity<String> getMerchantTinInfo(String tinNumber) throws ResourceNotFoundException {
        RequestBuilder builder = new RequestBuilder("GET");
        builder.addHeader("Content-Type", "application/json")
                .setHeader("Referer", refererHeader)
                .setUrl(URL + tinNumber + "/et")
                .build();
        JSONObject resp = httpProcessor.jsonRequestProcessor(builder);
        if (resp.getString("StatusCode").equals("200"))
            return ResponseEntity.ok(resp.getString("ResponseBody"));
        else if (resp.getString("StatusCode").equals("204"))
            throw new ResourceNotFoundException("Unable to retrieve merchant TIN information with the provided TIN number.");
        else
            throw new RuntimeException("An unexpected error occurred while processing the request.");
    }
}
