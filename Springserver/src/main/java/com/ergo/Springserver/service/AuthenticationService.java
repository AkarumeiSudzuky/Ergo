package com.ergo.Springserver.service;

import com.ergo.Springserver.dto.JwtAuthenticationResponse;
import com.ergo.Springserver.dto.SignInRequest;
import com.ergo.Springserver.dto.SignUpRequest;
import com.ergo.Springserver.model.user.UserDao;
import com.ergo.Springserver.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ergo.Springserver.model.user.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserDao userService;
    @Autowired
    private JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    /**
     * Handles user sign-up, creates a new user and generates a JWT token.
     * @param request the sign-up request containing user details
     * @return JwtAuthenticationResponse with the generated JWT token
     */
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userService.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    /**
     * Handles user sign-in and generates a JWT token upon successful authentication.
     * @param request the sign-in request containing username and password
     * @return JwtAuthenticationResponse with the generated JWT token
     */
    public JwtAuthenticationResponse signin(SignInRequest request) {
        log.info("Attempting to sign in user: {}", request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        log.info("User pass: {}", request.getPassword());
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        log.info("User found: {}", user.getUsername());

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    /**
     * Retrieves the user associated with the given JWT token.
     * @param token the JWT token
     * @return the user associated with the token
     */
    public User getUserFromToken(String token) {
        String username = jwtService.extractUserName(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }


}
