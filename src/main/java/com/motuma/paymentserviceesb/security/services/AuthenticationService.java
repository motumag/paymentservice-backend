package com.motuma.paymentserviceesb.security.services;

import com.motuma.paymentserviceesb.security.model.User;
import com.motuma.paymentserviceesb.security.payload.AuthenticationRequest;
import com.motuma.paymentserviceesb.security.payload.AuthenticationResponse;
import com.motuma.paymentserviceesb.security.payload.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest registerRequest);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void saveUserToken(User user, String jwtToken);
    void revokeAllUserTokens(User user);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
