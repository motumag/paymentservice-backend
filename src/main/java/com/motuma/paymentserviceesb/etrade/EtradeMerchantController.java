package com.motuma.paymentserviceesb.etrade;

import com.motuma.paymentserviceesb.security.exception.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
//@Tag(name = "Service APIs.")
public class EtradeMerchantController {
    private final MerchantTinService merchantTinService;
    public EtradeMerchantController(MerchantTinService merchantTinService) {
        this.merchantTinService = merchantTinService;
    }

    @GetMapping(value = "/tin-number-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkTinNumber(@RequestParam String tinNumber) throws ResourceNotFoundException {
        return merchantTinService.getMerchantTinInfo(tinNumber);
    }
}
