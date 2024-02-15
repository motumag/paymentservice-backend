package com.motuma.paymentserviceesb.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// TODO: 3. Find if user is exist with this username in the database
// TODO: 4. If the user is exist, reply with code 409 and user is already exist message
// TODO: 5. If user is not exist, handover to JwtService to check the validity of token
// TODO: 6. If jwt token is valid then allow to handover to controller and login
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authenticationHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmailOrPhone;
            // TODO: 1. Check if the token is exist
            if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authenticationHeader.substring(7);
            // TODO: 2. Extract the username from the token, Just implement JwtService class to manipulate this.
            userEmailOrPhone = jwtService.extractUserEmailOrPhoneFromJwt(jwt);
//            TODO: Now check if the user is not null and authenticated
//            if(userEmailOrPhone!=null&& !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
            if (userEmailOrPhone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmailOrPhone);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
            filterChain.doFilter(request,response);

        } catch (Exception ex) {
            System.out.println("The error is: " + ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }

    }

}
