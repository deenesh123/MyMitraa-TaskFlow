package com.mymitraa.taskflow.security;

import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DomainUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating user: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        if (user == null) {
            log.warn("User '{}' was not found in MongoDB mymitraa_taskflow", username);
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }
        return new CustomUserDetails(user);
    }
}
