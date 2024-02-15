package com.motuma.paymentserviceesb.payment.repository;

import com.motuma.paymentserviceesb.payment.model.OtpSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SendOtpRepository extends JpaRepository<OtpSend, Long> {
//    @Query(value = "SELECT * FROM tb_otpsend WHERE otp_number = ?1", nativeQuery = true)
    OtpSend findOtpSendsByOtpNumberIgnoreCase(String otpNumber);
}
