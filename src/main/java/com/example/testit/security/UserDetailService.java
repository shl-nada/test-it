package com.example.testit.security;

import com.example.testit.model.User;
import com.example.testit.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        String pwd = (user.getPassword() == null || user.getPassword().isBlank())
                ? "{noop}password"
                : user.getPassword();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(pwd)
                .roles("USER")
                .build();
    }
}
