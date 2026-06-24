package com.ecommerce.service;

import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.dto.*;
import com.ecommerce.model.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();
        user = userRepository.save(user);
        UserDetails ud = userDetailsService.loadUserByUsername(user.getEmail());
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(ud))
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
        UserDetails ud = userDetailsService.loadUserByUsername(user.getEmail());
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(ud))
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        return toDTO(userRepository.save(user));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserDTO toDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId()); dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName()); dto.setLastName(u.getLastName());
        dto.setPhone(u.getPhone()); dto.setRole(u.getRole().name());
        dto.setIsActive(u.getIsActive()); dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }
}
