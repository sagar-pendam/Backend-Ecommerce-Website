package com.ecommerce.auth.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



public class UserPrinciple implements UserDetails {

    private final Users user;

    public UserPrinciple(Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // use role from DB (must start with ROLE_)
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // allow all accounts for now
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // allow all accounts for now
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // allow all accounts for now
    }

    @Override
    public boolean isEnabled() {
        return true; // allow all accounts for now
    }
}
