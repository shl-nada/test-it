package com.example.testit.security;

import com.example.testit.model.User;
import com.example.testit.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

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

        String role = (user.getRole() == null || user.getRole().isBlank())
                ? "ROLE_USER"
                : user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                pwd,
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
