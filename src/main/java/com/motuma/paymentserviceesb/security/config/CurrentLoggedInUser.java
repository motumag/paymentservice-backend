package com.motuma.paymentserviceesb.security.config;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@Data
public class CurrentLoggedInUser {
    public String getCurrentUserSub() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            String sub = userDetails.getUsername();
            if (sub != null && !sub.isEmpty()) {
                return sub;
            } else {
                throw new IllegalStateException("UserDetails do not contain a valid 'sub' value");
            }
        }
        throw new IllegalStateException("No user is authenticated or UserDetails do not contain 'sub'");
    }
}
