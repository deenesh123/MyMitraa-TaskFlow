package com.mymitraa.taskflow.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.mymitraa.taskflow.api_domain.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public String getId() {
        return user != null ? user.getId() : null;
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getName() {
        return user != null ? user.getName() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null || user.getRoles() == null) {
            return java.util.Collections.emptyList();
        }
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user != null && user.isEnabled();
    }

    public User getUser() {
        return user;
    }
}