package com.ecommerce.auth.service;

import java.util.HashMap;
import java.util.Map;
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

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository repo;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        if (repo.existsByUsername(user.getUsername())) {
            throw new ApiException("Username already taken", HttpStatus.CONFLICT);
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public Map<String, String> verify(Users user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // ✅ Check authenticated status
            if (!authentication.isAuthenticated()) {
                throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = jwtService.generateAccessToken(user.getEmail());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;

        } catch (BadCredentialsException e) {
            throw new ApiException("Invalid email or password!", HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Add this temporarily for debugging
            throw new ApiException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }
    
   public Users getUserInfoByEmail(String email)
    {
    	return repo.findByEmail(email).get();
    }

}
