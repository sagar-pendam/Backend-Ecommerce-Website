package com.ecommerce.auth.rest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.auth.model.Users;
import com.ecommerce.auth.service.JWTService;
import com.ecommerce.auth.service.UserService;

@RestController
@RequestMapping("/auth-api")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private JWTService jwtService;
	@Autowired
	private UserService service;
	@PostMapping("/register")
	
	public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Users user) {
		 logger.info("Received registration request for email: {}", user.getEmail());
		try {
	        Users savedUser = service.register(user);
	        logger.info("User registered successfully: {}", savedUser.getEmail());
	        Map<String, Object> response = new HashMap<>();
	        response.put("status", "success");
	        response.put("message", "Registration successful");
	        response.put("user", savedUser);

	        return ResponseEntity.ok(response);
	    } catch (RuntimeException e) {
	    	logger.warn("Registration failed: {}", e.getMessage());
	        // Example: "Email already registered"
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of("status", "error", "message", e.getMessage()));
	    }
	}

	


	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Users user) {
	    try {
	    	  logger.info("Login attempt for email: {}", user.getEmail());
	        // Verify credentials and generate tokens
	        Map<String, String> tokens = service.verify(user);

	        // Fetch the user details (to include ID & email)
	        Users loggedInUser = service.getUserInfoByEmail(user.getEmail());
	        logger.info("Login successful for user: {}", loggedInUser.getEmail());
	        Map<String, Object> response = new HashMap<>();
	        response.put("status", "success");
	        response.put("message", "Login successful");
	        response.put("accessToken", tokens.get("accessToken"));
	        response.put("refreshToken", tokens.get("refreshToken"));
	        response.put("userId", loggedInUser.getId());
	        response.put("email", loggedInUser.getEmail());
	        response.put("username", loggedInUser.getUsername());

	        return ResponseEntity.ok(response);
	    } 
	    catch (UsernameNotFoundException e) {
	    	logger.error("Login failed: user not found");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("message", "User not found"));
	    } 
	    catch (BadCredentialsException e) {
	    	logger.error("Login failed: bad credentials");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("message", "Invalid email or password"));
	    } 
	    catch (Exception e) {
	    	 logger.error("Unexpected login error: {}", e.getMessage(), e);
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("message", "Unexpected error: " + e.getMessage()));
	    }
	}

	@PostMapping("/refresh-token")
	public Map<String, String> refreshToken(@RequestBody Map<String, String> request) {
		  logger.info("Refreshing token...");
	    String refreshToken = request.get("refreshToken");
	    String username = jwtService.extractUserName(refreshToken);
	    logger.info("Refresh token validated for: {}", username);
	    if (jwtService.isTokenExpired(refreshToken)) {
	    	logger.warn("Refresh token expired for user: {}", username);
	        throw new RuntimeException("Refresh token expired, please login again");
	    }

	    String newAccessToken = jwtService.generateAccessToken(username);

	    Map<String, String> response = new HashMap<>();
	    response.put("accessToken", newAccessToken);
	    response.put("refreshToken", refreshToken); // reuse same refresh token
	    return response;
	}

	
    
}
