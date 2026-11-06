package com.mymitraa.taskflow.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.mymitraa.taskflow.api_domain.User;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user != null && user.getRole() != null && !user.getRole().trim().isEmpty()) {
            String roleName = user.getRole().trim().toUpperCase().replace(" ", "_");
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            return Collections.singletonList(new SimpleGrantedAuthority(roleName));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : "anonymous";
    }

    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : "";
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