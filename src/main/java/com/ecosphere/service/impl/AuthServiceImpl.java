package com.ecosphere.service.impl;

import com.ecosphere.dto.*;
import com.ecosphere.entity.Role;
import com.ecosphere.entity.User;
import com.ecosphere.repository.UserRepository;
import com.ecosphere.service.AuthService;
import com.ecosphere.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.CITIZEN);

        userRepository.save(user);

        String token =
                jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!matches) {
            throw new RuntimeException("Invalid password");
        }

        String token =
                jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}