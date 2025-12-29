package com.ecommerce.apigateway.service;



import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	private static final Logger log = LoggerFactory.getLogger(JWTService.class);
    @Value("${jwt.secret}")
    private String secretkey;

    // Access token: 30 minutes
    public String generateAccessToken(String username) {
    	log.info("Generating Access Token for user: {}", username);
    	
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getKey())
                .compact();
    }

    // Refresh token: 7 days
    public String generateRefreshToken(String username) {
    	log.info("Generating Refresh Token for user: {}", username);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
    	log.debug("Decoding JWT secret key...");
        // Decode Base64 secret
        return Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secretkey));
    }

    public String extractUserName(String token) {
    	log.info("Extracting username from token...");
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token) {
    	log.info("Validating JWT token...");

        try {
            extractAllClaims(token); // will throw if invalid
            log.debug("Token validation successful.");
            return true;
        } catch (Exception e) {
        	log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
    	 log.debug("Extracting claims from token...");

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
