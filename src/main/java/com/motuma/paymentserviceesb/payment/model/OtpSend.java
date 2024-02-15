package com.motuma.paymentserviceesb.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_otpsend")
public class OtpSend {
    @Id
    @SequenceGenerator(
            name = "otp_Sequence",
            sequenceName = "otp_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "otp_Sequence"
    )
    private  Long id;
    private  String otpNumber;
    private  String accountNumber;
    private String phoneNumber;
    private  String status;
    private  String responseCode;
}
