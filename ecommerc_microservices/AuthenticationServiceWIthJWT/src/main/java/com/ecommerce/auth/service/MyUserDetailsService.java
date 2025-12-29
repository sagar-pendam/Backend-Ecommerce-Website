package com.ecommerce.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.model.UserPrinciple;
import com.ecommerce.auth.model.Users;
import com.ecommerce.auth.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	logger.info("Loading user by email: {}", email);
        Users user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        logger.info("User found: {}", user.getEmail());
        return new UserPrinciple(user);
    }
}
