package com.motuma.paymentserviceesb.security.payload;

import com.motuma.paymentserviceesb.security.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String userName;
    private String password;
    private String confirmPassword;
    private UserRole userRole;
}
