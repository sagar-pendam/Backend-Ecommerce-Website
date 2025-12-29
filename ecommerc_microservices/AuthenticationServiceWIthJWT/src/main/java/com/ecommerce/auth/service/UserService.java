package com.ecommerce.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.exception.ApiException;
import com.ecommerce.auth.model.Users;
import com.ecommerce.auth.repository.UserRepository;

@Service
public class UserService {
	 private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository repo;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
    	 logger.info("Registering new user: {}", user.getEmail());
        if (repo.existsByEmail(user.getEmail())) {
        	logger.warn("Registration failed: Email already exists");
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        if (repo.existsByUsername(user.getUsername())) {
        	logger.warn("Registration failed: Username already exists");
            throw new ApiException("Username already taken", HttpStatus.CONFLICT);
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
        	logger.info("Setting default role: ROLE_USER");
            user.setRole("ROLE_USER");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        logger.info("User registered successfully: {}", user.getEmail());
        return repo.save(user);
    }

    public Map<String, String> verify(Users user) {
    	  logger.info("Authenticating user: {}", user.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            //  Check authenticated status
            if (!authentication.isAuthenticated()) {
            	  logger.warn("Authentication failed for: {}", user.getEmail());
                throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }
            logger.info("Authentication successful: {}", user.getEmail());

            String accessToken = jwtService.generateAccessToken(user.getEmail());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;

        } catch (BadCredentialsException e) {
            logger.error("Bad credentials for user: {}", user.getEmail());
            throw new ApiException("Invalid email or password!", HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", user.getEmail());
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage(), e);
            throw new ApiException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }
    
   public Users getUserInfoByEmail(String email)
    {logger.info("Fetching user info by email: {}", email);
    	return repo.findByEmail(email).get();
    }

}
