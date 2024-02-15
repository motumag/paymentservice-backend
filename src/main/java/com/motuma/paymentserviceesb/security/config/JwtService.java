package com.motuma.paymentserviceesb.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String SECRET_KEY="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long JWT_EXPIRATION=86400000;
    private static final long REFRESH_TOKEN_EXPIRE=604800000;

    //    TODO: This is to get the username from the token and use in JwtAuthenticationFilter class to check if the user is in db
    public String extractUserEmailOrPhoneFromJwt(String tokenFromJwtAuthFilter) {
//        Here now we extracted the username from the token
        return extractSingleClaimFromAllClaim(tokenFromJwtAuthFilter, Claims::getSubject);
    }

    //    TODO: Extract single claim after extracting the all claim. Use the allClaim method.
    public <T> T extractSingleClaimFromAllClaim(String tokenFromJwtAuthFilter, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(tokenFromJwtAuthFilter);
        return claimsResolver.apply(claims);
    }

    //    TODO: Extract all the claims of the token from the request.
    public Claims extractAllClaims(String tokenFromJwtAuthFilter) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(tokenFromJwtAuthFilter)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
//    TODO: To generate token only with user details we can use like the bellow block of code
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, JWT_EXPIRATION);
    }
    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, REFRESH_TOKEN_EXPIRE);
    }
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setId(userDetails.getAuthorities().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

//    TODO: Check token validation[For this we need method isTokenExpired,extractExpiration to implement ]
    public boolean isTokenValid(String tokenFromJwtAuthFilter, UserDetails userDetails){
        final String userNameFromToken= extractUserEmailOrPhoneFromJwt(tokenFromJwtAuthFilter);
        return ((userNameFromToken.equals(userDetails.getUsername()))&& !isTokenExpired(tokenFromJwtAuthFilter));
    }

    private boolean isTokenExpired(String tokenFromJwtAuthFilter) {
        return  extractExpiration(tokenFromJwtAuthFilter).before(new Date());
    }

    private Date extractExpiration(String tokenFromJwtAuthFilter) {
        return extractSingleClaimFromAllClaim(tokenFromJwtAuthFilter,Claims::getExpiration);
    }
}
