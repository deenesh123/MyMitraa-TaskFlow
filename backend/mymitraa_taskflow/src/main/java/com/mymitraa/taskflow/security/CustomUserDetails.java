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
        if (user == null) {
            return java.util.Collections.emptyList();
        }
        java.util.Set<GrantedAuthority> authorities = new java.util.HashSet<>();
        if (user.getRole() != null && !user.getRole().trim().isEmpty()) {
            String roleStr = user.getRole().trim();
            authorities.add(new SimpleGrantedAuthority(roleStr));
            String normalized = "ROLE_" + roleStr.toUpperCase().replace(" ", "_");
            authorities.add(new SimpleGrantedAuthority(normalized));
        }
        if (user.getRoles() != null) {
            for (String r : user.getRoles()) {
                if (r != null && !r.trim().isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(r.trim()));
                    String normalized = "ROLE_" + r.trim().toUpperCase().replace(" ", "_");
                    authorities.add(new SimpleGrantedAuthority(normalized));
                }
            }
        }
        if (user.getPermissions() != null) {
            for (String p : user.getPermissions()) {
                if (p != null && !p.trim().isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(p.trim()));
                }
            }
        }
        return authorities;
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